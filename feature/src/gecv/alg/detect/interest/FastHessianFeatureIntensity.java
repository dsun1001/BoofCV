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

import gecv.alg.transform.ii.DerivativeIntegralImage;
import gecv.alg.transform.ii.IntegralImageOps;
import gecv.alg.transform.ii.IntegralKernel;
import gecv.struct.image.ImageFloat32;


/**
 * Routines for computing the intensity of the fast hessian features in an image.
 *
 * @author Peter Abeles
 */
public class FastHessianFeatureIntensity {

	/**
	 * Brute force approach which is easy to validate through visual inspection.
	 */
	public static void naive( ImageFloat32 integral, int skip , int size ,
							  ImageFloat32 intensity)
	{
		final int w = intensity.width;
		final int h = intensity.height;

		// get convolution kernels for the second order derivatives
		IntegralKernel kerXX = DerivativeIntegralImage.kernelDerivXX(size);
		IntegralKernel kerYY = DerivativeIntegralImage.kernelDerivYY(size);
		IntegralKernel kerXY = DerivativeIntegralImage.kernelDerivXY(size);

		float norm = 1.0f/(size*size);

		for( int y = 0; y < h; y++ ) {
			for( int x = 0; x < w; x++ ) {

				int xx = x*skip;
				int yy = y*skip;

				computeIntensity(integral, intensity, kerXX, kerYY, kerXY, norm, y, yy, x, xx);
			}
		}
	}

	/**
	 * Only computes the fast hessian along the border using a brute force approach
	 */
	public static void border( ImageFloat32 integral, int skip , int size ,
							   ImageFloat32 intensity)
	{
		final int w = intensity.width;
		final int h = intensity.height;

		// get convolution kernels for the second order derivatives
		IntegralKernel kerXX = DerivativeIntegralImage.kernelDerivXX(size);
		IntegralKernel kerYY = DerivativeIntegralImage.kernelDerivYY(size);
		IntegralKernel kerXY = DerivativeIntegralImage.kernelDerivXY(size);

		int radiusFeature = size/2;
		final int borderOrig = radiusFeature+ 1 + (skip-(radiusFeature+1)%skip);
		final int border = borderOrig/skip;

		float norm = 1.0f/(size*size);

		for( int y = 0; y < h; y++ ) {
			int yy = y*skip;
			for( int x = 0; x < border; x++ ) {
				int xx = x*skip;
				computeIntensity(integral, intensity, kerXX, kerYY, kerXY, norm, y, yy, x, xx);
			}
			for( int x = w-border; x < w; x++ ) {
				int xx = x*skip;
				computeIntensity(integral, intensity, kerXX, kerYY, kerXY, norm, y, yy, x, xx);
			}
		}

		for( int x = border; x < w-border; x++ ) {
			int xx = x*skip;

			for( int y = 0; y < border; y++ ) {
				int yy = y*skip;
				computeIntensity(integral, intensity, kerXX, kerYY, kerXY, norm, y, yy, x, xx);
			}
			for( int y = h-border; y < h; y++ ) {
				int yy = y*skip;
				computeIntensity(integral, intensity, kerXX, kerYY, kerXY, norm, y, yy, x, xx);
			}
		}
	}

	private static void computeIntensity(ImageFloat32 integral, ImageFloat32 intensity, IntegralKernel kerXX, IntegralKernel kerYY, IntegralKernel kerXY, float norm, int y, int yy, int x, int xx) {
		float Dxx = IntegralImageOps.convolveSparse(integral,kerXX,xx,yy);
		float Dyy = IntegralImageOps.convolveSparse(integral,kerYY,xx,yy);
		float Dxy = IntegralImageOps.convolveSparse(integral,kerXY,xx,yy);

		Dxx *= norm;
		Dxy *= norm;
		Dyy *= norm;

		float det = Dxx*Dyy-0.81f*Dxy*Dxy;

		intensity.set(x,y,det);
	}

	/**
	 * Optimizes intensity for the inner image.  
	 * @param integral
	 * @param skip
	 * @param size
	 * @param intensity
	 */
	public static void inner( ImageFloat32 integral, int skip , int size ,
							  ImageFloat32 intensity)
	{
		final int w = intensity.width;
		final int h = intensity.height;

		float norm = 1.0f/(size*size);

		int blockSmall = size/3;
		int blockLarge = size-blockSmall-1;
		int radiusFeature = size/2;
		int radiusSkinny = blockLarge/2;

		int blockW2 = 2*blockSmall;
		int blockW3 = 3*blockSmall;


		int rowOff1 = blockSmall*integral.stride;
		int rowOff2 = 2*rowOff1;
		int rowOff3 = 3*rowOff1;

		// make sure it starts on the correct pixel
		final int borderOrig = radiusFeature+ 1 + (skip-(radiusFeature+1)%skip);
		final int border = borderOrig/skip;
		final int lostPixel = borderOrig - radiusFeature-1;
		final int endY = h - border;
		final int endX = w - border;

		for( int y = border; y < endY; y++ ) {

			// pixel location in original input image
			int yy = y*skip;

			// index for output
			int indexDst = intensity.startIndex + y*intensity.stride+border;

			// indexes for Dxx
			int indexTop = integral.startIndex + (yy-radiusSkinny-1)*integral.stride+lostPixel;
			int indexBottom = indexTop + (blockLarge)*integral.stride;

			// indexes for Dyy
			int indexL = integral.startIndex + (yy-radiusFeature-1)*integral.stride + (radiusFeature-radiusSkinny)+lostPixel;
			int indexR = indexL + blockLarge;

			// indexes for Dxy
			int indexY1 = integral.startIndex + (yy-blockSmall-1)*integral.stride + (radiusFeature-blockSmall)+lostPixel;
			int indexY2 = indexY1 + blockSmall*integral.stride;
			int indexY3 = indexY2 + integral.stride;
			int indexY4 = indexY3 + blockSmall*integral.stride;

			for( int x = border; x < endX; x++ , indexDst++) {
				float Dxx = integral.data[indexBottom+blockW3] - integral.data[indexTop+blockW3] - integral.data[indexBottom] + integral.data[indexTop];
				Dxx -= 3*(integral.data[indexBottom+blockW2] - integral.data[indexTop+blockW2] - integral.data[indexBottom+blockSmall] + integral.data[indexTop+blockSmall]);

				float Dyy = integral.data[indexR+rowOff3] - integral.data[indexL+rowOff3] - integral.data[indexR] + integral.data[indexL];
				Dyy -= 3*(integral.data[indexR+rowOff2] - integral.data[indexL+rowOff2] - integral.data[indexR+rowOff1] + integral.data[indexL+rowOff1]);

				int x3 = blockSmall+1;
				int x4 = x3+blockSmall;

				float Dxy = integral.data[indexY2+blockSmall] - integral.data[indexY1+blockSmall] - integral.data[indexY2] + integral.data[indexY1];
				Dxy -= integral.data[indexY2+x4] - integral.data[indexY1+x4] - integral.data[indexY2+x3] + integral.data[indexY1+x3];
				Dxy += integral.data[indexY4+x4] - integral.data[indexY3+x4] - integral.data[indexY4+x3] + integral.data[indexY3+x3];
				Dxy -= integral.data[indexY4+blockSmall] - integral.data[indexY3+blockSmall] - integral.data[indexY4] + integral.data[indexY3];

				Dxx *= norm;
				Dxy *= norm;
				Dyy *= norm;

				intensity.data[indexDst] = Dxx*Dyy-0.81f*Dxy*Dxy;

				indexTop += skip;
				indexBottom += skip;
				indexL += skip;
				indexR += skip;
				indexY1 += skip;
				indexY2 += skip;
				indexY3 += skip;
				indexY4 += skip;
			}
		}

	}

	/**
	 * Computes the feature intensity for the fast-hessian detector
	 * @param integral
	 * @param skip
	 * @param size
	 * @param intensity
	 */
	public static void intensity( ImageFloat32 integral, int skip , int size ,
								  ImageFloat32 intensity)
	{
		// todo check size with skip
//		InputSanityCheck.checkSameShape(integral,intensity);

		border(integral,skip,size,intensity);
		inner(integral,skip,size,intensity);
	}
}
