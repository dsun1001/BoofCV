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

package boofcv.abst.filter.convolve;

import boofcv.abst.filter.ImageFunctionSparse;
import boofcv.core.image.border.ImageBorder;
import boofcv.struct.convolve.Kernel2D;
import boofcv.struct.image.ImageSingleBand;

/**
 * Abstract class for sparse image convolution.  A convolution is performed at the specified point.
 *
 * @author Peter Abeles
 */
public abstract class ImageConvolveSparse< T extends ImageSingleBand, K extends Kernel2D > implements ImageFunctionSparse<T> {

	// kernel being convolved
	protected K kernel;
	// image wrapper to handle the image borders
	protected ImageBorder<T> image;

	protected ImageConvolveSparse(K kernel) {
		this.kernel = kernel;
	}

	protected ImageConvolveSparse() {
	}

	public void setImageBorder(ImageBorder<T> image) {
		this.image = image;
	}

	public void setKernel( K kernel ) {
		this.kernel = kernel;
	}

	@Override
	public void setImage(T image) {
		this.image.setImage(image);
	}
}
