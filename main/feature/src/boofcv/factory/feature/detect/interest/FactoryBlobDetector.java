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

package boofcv.factory.feature.detect.interest;

import boofcv.abst.feature.detect.extract.GeneralFeatureDetector;
import boofcv.abst.feature.detect.intensity.GeneralFeatureIntensity;
import boofcv.abst.feature.detect.intensity.WrapperLaplacianBlobIntensity;
import boofcv.alg.feature.detect.intensity.HessianBlobIntensity;
import boofcv.struct.image.ImageSingleBand;


/**
 * Creates feature detectors which detect "blob" like objects.
 *
 * @author Peter Abeles
 */
public class FactoryBlobDetector {

	public static <T extends ImageSingleBand, D extends ImageSingleBand>
	GeneralFeatureDetector<T,D> createLaplace( int featureRadius , float pixelTol , int maxFeatures ,
											   Class<D> derivType , HessianBlobIntensity.Type type )
	{
		GeneralFeatureIntensity<T,D> intensity = new WrapperLaplacianBlobIntensity<T,D>(type,derivType);
		return FactoryCornerDetector.createGeneral(intensity,featureRadius,pixelTol,maxFeatures);
	}
}
