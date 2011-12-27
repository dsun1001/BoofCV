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

package boofcv.alg.filter.convolve.noborder;

import boofcv.struct.convolve.Kernel1D_F32;
import boofcv.struct.convolve.Kernel1D_I32;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

/**
 *
 * <p>
 * General implementation of {@link boofcv.alg.filter.convolve.ConvolveImageNoBorderSparse}.
 * </p>
 *
 * <p>
 * DO NOT MODIFY.  Auto generated by {@link GenerateConvolveStandardSparse}.
 * </p>
 *
 * @author Peter Abeles
 */
public class ConvolveImageStandardSparse {

	public static float convolve( Kernel1D_F32 horizontal, Kernel1D_F32 vertical,
								ImageFloat32 input, int c_x , int c_y, float storage[] )
	{
//		if( c_x == 2 && c_y == 2 )
//			System.out.println("stp here");

		// convolve horizontally first
		int width = horizontal.getWidth();
		int radius = width/2;

		for( int i = 0; i < width; i++ ) {
			int indexImg = input.startIndex + (i+c_y-radius)*input.stride + c_x-radius;

			float total = 0;
			for( int j = 0; j < width; j++ ,indexImg++) {
				total += (input.data[indexImg])*horizontal.data[j];
			}
			storage[i] = total;
		}

		// convolve vertically
		float total = 0;
		for( int i = 0; i < width; i++ ) {
			total += storage[i]*vertical.data[i];
		}
		return total;
	}

	public static int convolve( Kernel1D_I32 horizontal, Kernel1D_I32 vertical,
								ImageUInt8 input, int c_x , int c_y, int storage[] )
	{
		// convolve horizontally first
		int width = horizontal.getWidth();
		int radius = width/2;

		for( int i = 0; i < width; i++ ) {
			int indexImg = input.startIndex + (i+c_y-radius)*input.stride + c_x-radius;

			int total = 0;
			for( int j = 0; j < width; j++ ,indexImg++) {
				total += (input.data[indexImg] & 0xFF)*horizontal.data[j];
			}
			storage[i] = total;
		}

		// convolve vertically
		int total = 0;
		for( int i = 0; i < width; i++ ) {
			total += storage[i]*vertical.data[i];
		}
		return total;
	}

	public static int convolve( Kernel1D_I32 horizontal, Kernel1D_I32 vertical,
								ImageUInt8 input, int c_x , int c_y, int storage[] ,
								int divisorHorizontal ,
								int divisorVertical )
	{
		// convolve horizontally first
		int width = horizontal.getWidth();
		int radius = width/2;

		for( int i = 0; i < width; i++ ) {
			int indexImg = input.startIndex + (i+c_y-radius)*input.stride + c_x-radius;

			int total = 0;
			for( int j = 0; j < width; j++ ,indexImg++) {
				total += (input.data[indexImg] & 0xFF)*horizontal.data[j];
			}
			storage[i] = total/divisorHorizontal;
		}

		// convolve vertically
		int total = 0;
		for( int i = 0; i < width; i++ ) {
			total += storage[i]*vertical.data[i];
		}
		return total/divisorVertical;
	}

	public static int convolve( Kernel1D_I32 horizontal, Kernel1D_I32 vertical,
								ImageSInt16 input, int c_x , int c_y, int storage[] )
	{
		// convolve horizontally first
		int width = horizontal.getWidth();
		int radius = width/2;

		for( int i = 0; i < width; i++ ) {
			int indexImg = input.startIndex + (i+c_y-radius)*input.stride + c_x-radius;

			int total = 0;
			for( int j = 0; j < width; j++ ,indexImg++) {
				total += (input.data[indexImg])*horizontal.data[j];
			}
			storage[i] = total;
		}

		// convolve vertically
		int total = 0;
		for( int i = 0; i < width; i++ ) {
			total += storage[i]*vertical.data[i];
		}
		return total;
	}

	public static int convolve( Kernel1D_I32 horizontal, Kernel1D_I32 vertical,
								ImageSInt16 input, int c_x , int c_y, int storage[] ,
								int divisorHorizontal ,
								int divisorVertical )
	{
		// convolve horizontally first
		int width = horizontal.getWidth();
		int radius = width/2;

		for( int i = 0; i < width; i++ ) {
			int indexImg = input.startIndex + (i+c_y-radius)*input.stride + c_x-radius;

			int total = 0;
			for( int j = 0; j < width; j++ ,indexImg++) {
				total += (input.data[indexImg])*horizontal.data[j];
			}
			storage[i] = total/divisorHorizontal;
		}

		// convolve vertically
		int total = 0;
		for( int i = 0; i < width; i++ ) {
			total += storage[i]*vertical.data[i];
		}
		return total/divisorVertical;
	}

}
