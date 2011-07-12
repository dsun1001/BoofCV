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

package gecv.abst.wavelet.impl;

import gecv.alg.wavelet.FactoryWaveletDaub;
import gecv.alg.wavelet.UtilWavelet;
import gecv.alg.wavelet.WaveletTransformOps;
import gecv.core.image.ConvertImage;
import gecv.core.image.GeneralizedImageOps;
import gecv.core.image.border.BorderType;
import gecv.struct.image.ImageDimension;
import gecv.struct.image.ImageSInt32;
import gecv.struct.image.ImageUInt8;
import gecv.struct.wavelet.WaveletDescription;
import gecv.struct.wavelet.WlCoef_I32;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class TestWaveletTransformInt {

	Random rand = new Random(3445);
	int width = 30;
	int height = 40;

	@Test
	public void compareToWaveletTransformOps() {
		ImageSInt32 orig = new ImageSInt32(width,height);
		GeneralizedImageOps.randomize(orig,rand,0,20);
		ImageSInt32 origCopy = orig.clone();

		int N = 3;
		ImageDimension dimen = UtilWavelet.transformDimension(orig,N);

		ImageSInt32 found = new ImageSInt32(dimen.width,dimen.height);
		ImageSInt32 expected = new ImageSInt32(dimen.width,dimen.height);

 		WaveletDescription<WlCoef_I32> desc = FactoryWaveletDaub.biorthogonal_I32(5, BorderType.REFLECT);

		ImageSInt32 storage = new ImageSInt32(dimen.width,dimen.height);
		WaveletTransformOps.transformN(desc,orig.clone(),expected,storage,N);

		WaveletTransformInt<ImageSInt32> alg = new WaveletTransformInt<ImageSInt32>(desc,N);
		alg.transform(orig,found);

		// make sure the original input was not modified like it is in WaveletTransformOps
		GecvTesting.assertEquals(origCopy,orig, 0);
		// see if the two techniques produced the same results
		GecvTesting.assertEquals(expected,found, 0);

		// test inverse transform
		ImageSInt32 reconstructed = new ImageSInt32(width,height);
		alg.invert(found,reconstructed);
		GecvTesting.assertEquals(orig,reconstructed, 0);
		// make sure the input has not been modified
		GecvTesting.assertEquals(expected,found, 0);
	}

	/**
	 * See how well it processes an image which is not an ImageSInt32
	 */
	@Test
	public void checkOtherType() {
		ImageSInt32 orig = new ImageSInt32(width,height);
		GeneralizedImageOps.randomize(orig,rand,0,20);
		ImageUInt8 orig8 = ConvertImage.convert(orig,(ImageUInt8)null);

		int N = 3;
		ImageDimension dimen = UtilWavelet.transformDimension(orig,N);

		ImageSInt32 found = new ImageSInt32(dimen.width,dimen.height);
		ImageSInt32 expected = new ImageSInt32(dimen.width,dimen.height);

		WaveletDescription<WlCoef_I32> desc = FactoryWaveletDaub.biorthogonal_I32(5, BorderType.REFLECT);

		ImageSInt32 storage = new ImageSInt32(dimen.width,dimen.height);
		WaveletTransformOps.transformN(desc,orig.clone(),expected,storage,N);

		WaveletTransformInt<ImageUInt8> alg = new WaveletTransformInt<ImageUInt8>(desc,N);
		alg.transform(orig8,found);

		// see if the two techniques produced the same results
		GecvTesting.assertEquals(expected,found, 0);

		// see if it can convert it back
		ImageUInt8 reconstructed = new ImageUInt8(width,height);
		alg.invert(found,reconstructed);
		GecvTesting.assertEquals(orig8,reconstructed, 0);
		// make sure the input has not been modified
		GecvTesting.assertEquals(expected,found, 0);
	}
}