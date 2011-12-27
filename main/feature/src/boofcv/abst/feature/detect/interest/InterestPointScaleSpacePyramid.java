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

package boofcv.abst.feature.detect.interest;

import boofcv.alg.transform.gss.ScaleSpacePyramid;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.ImageSingleBand;

import java.util.List;


/**
 * Interest point detector for {@link ScaleSpacePyramid Scale Space Pyramid} images.
 *
 * @author Peter Abeles
 */
public interface InterestPointScaleSpacePyramid<T extends ImageSingleBand> {

	/**
	 * Detect features in the scale space image
	 *
	 * @param ss Scale space of an image
	 */
	public void detect( ScaleSpacePyramid<T> ss );

	/**
	 * Returns all the found interest points
	 *
	 * @return List of found interest points.
	 */
	public List<ScalePoint> getInterestPoints();
}
