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

package gecv.alg.detect.interest;

import gecv.alg.transform.ii.IntegralImageOps;
import gecv.core.image.GeneralizedImageOps;
import gecv.struct.image.ImageFloat32;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class TestFastHessianFeatureIntensity {

	Random rand = new Random(234);
	int width = 60;
	int height = 70;

	/**
	 * Compares the inner() function against the output from the naive function.
	 */
	@Test
	public void inner() {
		ImageFloat32 original = new ImageFloat32(width,height);
		ImageFloat32 integral = new ImageFloat32(width,height);
		ImageFloat32 found = new ImageFloat32(width,height);
		ImageFloat32 expected = new ImageFloat32(width,height);

		GeneralizedImageOps.randomize(original,rand,0,50);
		IntegralImageOps.transform(original,integral);

		int size = 9;
		int r = size/2+1;
		r++;
		for( int skip = 1; skip <= 4; skip++ ) {
			found.reshape(width/skip,height/skip);
			expected.reshape(width/skip,height/skip);
			FastHessianFeatureIntensity.naive(integral,skip,size,expected);
			FastHessianFeatureIntensity.inner(integral,skip,size,found);

			int w = found.width;
			int h = found.height;
			ImageFloat32 f = found.subimage(r+1,r+1,w-r,h-r);
			ImageFloat32 e = expected.subimage(r+1,r+1,w-r,h-r);

			GecvTesting.assertEquals(e,f,0,1e-4f);
		}
	}

	/**
	 * Compares the inner() function against the output from the naive function.
	 */
	@Test
	public void intensity() {
		ImageFloat32 original = new ImageFloat32(width,height);
		ImageFloat32 integral = new ImageFloat32(width,height);
		ImageFloat32 found = new ImageFloat32(width,height);
		ImageFloat32 expected = new ImageFloat32(width,height);

		GeneralizedImageOps.randomize(original,rand,0,50);
		IntegralImageOps.transform(original,integral);

		int size = 9;

		for( int skip = 1; skip <= 4; skip++ ) {
			found.reshape(width/skip,height/skip);
			expected.reshape(width/skip,height/skip);
			FastHessianFeatureIntensity.naive(integral,skip,size,expected);
			FastHessianFeatureIntensity.intensity(integral,skip,size,found);

			GecvTesting.assertEquals(expected,found,0,1e-4f);
		}
	}
}
