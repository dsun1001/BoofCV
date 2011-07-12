/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.abst.filter.blur.impl;

import gecv.abst.filter.FilterImageInterface;
import gecv.alg.filter.blur.BlurImageOps;
import gecv.struct.image.ImageBase;
import gecv.testing.GecvTesting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simplified interface for using a blur filter that requires storage.  Reflections are used to look up a function inside
 * of {@link gecv.alg.filter.blur.BlurImageOps} which is then invoked later on.
 *
 * @author Peter Abeles
 */
public class BlurStorageFilter<T extends ImageBase> implements FilterImageInterface<T,T> {

	// the blur function inside of BlurImageOps being invoked
	private Method m;
	// size of the blur region
	private int radius;
	// stores intermediate results
	private ImageBase storage;

	public BlurStorageFilter( String functionName , Class<?> imageType , int radius) {
		this.radius = radius;

		m = GecvTesting.findMethod(BlurImageOps.class,functionName,imageType,imageType,imageType,int.class);

		if( m == null )
			throw new IllegalArgumentException("Can't find matching function for image type "+imageType.getSimpleName());
	}

	/**
	 * Radius of the square region.  The width is defined as the radius*2 + 1.
	 *
	 * @return Blur region's radius.
	 */
	public int getRadius() {
		return radius;
	}

	@Override
	public void process(T input, T output) {
		try {
			if( storage == null ) {
				storage = output._createNew(output.width,output.height);
			} else {
				storage.reshape(output.width,output.height);
			}
			m.invoke(null,input,output,storage,radius);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getHorizontalBorder() {
		return 0;
	}

	@Override
	public int getVerticalBorder() {
		return 0;
	}
}