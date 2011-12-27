/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.struct.image;

import boofcv.core.image.GeneralizedImageOps;

import java.lang.reflect.Array;

/**
 * Image class for images that are composed of multiple bands.  Each band is a gray scale image of the same type.
 * Each of these images has the same width, height, startIndex, and stride.
 *
 * @author Peter Abeles
 */
public class MultiSpectral<T extends ImageSingleBand> extends ImageBase<MultiSpectral<T>>{

	/**
	 * Type of image in each band
	 */
	public Class<T> type;

	/**
	 * Set of gray scale images
	 */
	public T bands[];

	public MultiSpectral(Class<T> type, int width, int height, int numBands) {
		this.type = type;
		this.stride = width;
		this.width = width;
		this.height = height;
		bands = (T[]) Array.newInstance(type, numBands);

		for (int i = 0; i < numBands; i++) {
			bands[i] = GeneralizedImageOps.createSingleBand(type, width, height);
		}
	}

	protected MultiSpectral(Class<T> type, int numBands) {
		this.type = type;
		bands = (T[]) Array.newInstance(type, numBands);
	}
	
	

	public Class<T> getType() {
		return type;
	}

	public int getNumBands() {
		return bands.length;
	}

	public T getBand(int band) {
		if (band >= bands.length || band < 0)
			throw new IllegalArgumentException("The specified band is out of bounds");

		return bands[band];
	}

	/**
	 * Creates a sub-image from 'this' image.  The subimage will share the same internal array
	 * that stores each pixel's value, but will only pertain to an axis-aligned rectangular segment
	 * of the original.
	 *
	 * @param x0 x-coordinate of top-left corner of the sub-image.
	 * @param y0 y-coordinate of top-left corner of the sub-image.
	 * @param x1 x-coordinate of bottom-right corner of the sub-image.
	 * @param y1 y-coordinate of bottom-right corner of the sub-image.
	 * @return A sub-image of this image.
	 */
	@Override
	public MultiSpectral<T> subimage(int x0, int y0, int x1, int y1) {
		if (x0 < 0 || y0 < 0)
			throw new IllegalArgumentException("x0 or y0 is less than zero");
		if (x1 < x0 || y1 < y0)
			throw new IllegalArgumentException("x1 or y1 is less than x0 or y0 respectively");
		if (x1 > width || y1 > height)
			throw new IllegalArgumentException("x1 or y1 is more than the width or height respectively");

		MultiSpectral<T> ret = new MultiSpectral<T>(type,bands.length);
		ret.stride = Math.max(width, stride);
		ret.width = x1 - x0;
		ret.height = y1 - y0;
		ret.startIndex = startIndex + y0 * stride + x0;

		for( int i = 0; i < bands.length; i++ ) {
			ret.bands[i] = (T)bands[i].subimage(x0,y0,x1,y1);
		}
		
		return ret;
	}

	/**
	 * Changes the image's width and height without declaring new memory.  If the internal array
	 * is not large enough to store the new image an IllegalArgumentException is thrown.
	 *
	 * @param width The new width.
	 * @param height The new height.
	 */
	@Override
	public void reshape(int width, int height) {

		for( int i = 0; i < bands.length; i++ ) {
			bands[i].reshape(width,height);
		}

		this.startIndex = 0;
		this.stride = width;
		this.width = width;
		this.height = height;
	}
}
