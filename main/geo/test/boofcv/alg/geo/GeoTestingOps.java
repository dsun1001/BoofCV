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

package boofcv.alg.geo;


import georegression.struct.point.Point3D_F64;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Operations useful for unit tests
 *
 * @author Peter Abeles
 */
public class GeoTestingOps {

	public static List<Point3D_F64> randomPoints_F32( double minX , double maxX ,
													  double minY , double maxY ,
													  double minZ , double maxZ ,
													  int num , Random rand )
	{
		List<Point3D_F64> ret = new ArrayList<Point3D_F64>();

		for( int i = 0; i < num; i++ ) {
			double x = rand.nextDouble()*(maxX-minX)+minX;
			double y = rand.nextDouble()*(maxY-minY)+minY;
			double z = rand.nextDouble()*(maxZ-minZ)+minZ;

			ret.add(new Point3D_F64(x,y,z));
		}

		return ret;
	}
}
