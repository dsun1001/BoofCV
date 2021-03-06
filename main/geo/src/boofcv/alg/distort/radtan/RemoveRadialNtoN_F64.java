/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
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

package boofcv.alg.distort.radtan;

import boofcv.struct.distort.PointTransform_F64;
import georegression.struct.point.Point2D_F64;

/**
 * Converts the observed distorted normalized image coordinates into undistorted normalized image coordinates.
 *
 * @author Peter Abeles
 */
public class RemoveRadialNtoN_F64 implements PointTransform_F64 {

	// distortion parameters
	protected RadialTangential_F64 params;

	// radial distortion magnitude
	protected double sum;
	// found tangential distortion
	protected double tx,ty;

	private double tol=1e-10;

	public RemoveRadialNtoN_F64() {
	}

	public RemoveRadialNtoN_F64(double tol) {
		this.tol = tol;
	}

	public void setTolerance(double tol) {
		this.tol = tol;
	}

	public RemoveRadialNtoN_F64 setDistortion( double[] radial, double t1, double t2 ) {
		params = new RadialTangential_F64(radial,t1,t2);
		return this;
	}

	/**
	 * Removes radial distortion
	 *
	 * @param x Distorted x-coordinate normalized image coordinate
	 * @param y Distorted y-coordinate normalized image coordinate
	 * @param out Undistorted normalized coordinate.
	 */
	@Override
	public void compute(double x, double y, Point2D_F64 out)
	{
		double radial[] = params.radial;
		double t1 = params.t1,t2 = params.t2;

		double origX = x;
		double origY = y;

		double prevSum = 0;

		for( int iter = 0; iter < 20; iter++ ) {

			// estimate the radial distance
			double r2 = x*x + y*y;
			double ri2 = r2;

			sum = 0;
			for( int i = 0; i < radial.length; i++ ) {
				sum += radial[i]*ri2;
				ri2 *= r2;
			}

			tx = 2*t1*x*y + t2*(r2 + 2*x*x);
			ty = t1*(r2 + 2*y*y) + 2*t2*x*y;

			x = (origX - tx)/(1+sum);
			y = (origY - ty)/(1+sum);

			if( Math.abs(prevSum-sum) <= tol ) {
				break;
			} else {
				prevSum = sum;
			}
		}
		out.set(x,y);
	}
}