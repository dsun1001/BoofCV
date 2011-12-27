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

package boofcv.alg.interpolate.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateImplInterpolatePixelConvolution extends CodeGeneratorBase {

	PrintStream out;
	AutoTypeImage inputType;

	@Override
	public void generate() throws FileNotFoundException {
		createFile(AutoTypeImage.F32);
		createFile(AutoTypeImage.S16);
		createFile(AutoTypeImage.U8);
	}

	private void createFile( AutoTypeImage inputType ) throws FileNotFoundException {
		this.inputType = inputType;
		String className = "ImplInterpolatePixelConvolution_"+inputType.getAbbreviatedType();
		out = new PrintStream(new FileOutputStream(className + ".java"));

		printPreamble(className);

		printFuncs();

		out.print("\n" +
				"}\n");
	}

	private void printPreamble( String fileName ) {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package boofcv.alg.interpolate.impl;\n" +
				"\n" +
				"import boofcv.alg.interpolate.InterpolatePixel;\n" +
				"import boofcv.struct.convolve.KernelContinuous1D_F32;\n" +
				"import boofcv.struct.image.*;\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Performs interpolation by convolving a continuous-discrete function across the image.  Borders are handled by\n" +
				" * re-normalizing.  It is assumed that the kernel will sum up to one.  This is particularly\n" +
				" * important for the unsafe_get() function which does not re-normalize.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * DO NOT MODIFY: Generated by {@link GenerateImplInterpolatePixelConvolution}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+fileName+" implements InterpolatePixel<"+inputType.getImageName()+">  {\n" +
				"\n" +
				"\t// kernel used to perform interpolation\n" +
				"\tprivate KernelContinuous1D_F32 kernel;\n" +
				"\t// input image\n" +
				"\tprivate "+inputType.getImageName()+" image;\n" +
				"\t// minimum and maximum allowed pixel values\n" +
				"\tprivate float min,max;\n" +
				"\n" +
				"\tpublic "+fileName+"(KernelContinuous1D_F32 kernel , float min , float max ) {\n" +
				"\t\tthis.kernel = kernel;\n" +
				"\t\tthis.min = min;\n" +
				"\t\tthis.max = max;\n" +
				"\t}\n\n");
	}

	private void printFuncs() {

		String bitWise = inputType.getBitWise();

		out.print("\t@Override\n" +
				"\tpublic void setImage("+inputType.getImageName()+" image ) {\n" +
				"\t\tthis.image = image;\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic "+inputType.getImageName()+" getImage() {\n" +
				"\t\treturn image;\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic float get(float x, float y) {\n" +
				"\n" +
				"\t\tint xx = (int)x;\n" +
				"\t\tint yy = (int)y;\n" +
				"\n" +
				"\t\tfinal int radius = kernel.getRadius();\n" +
				"\t\tfinal int width = kernel.getWidth();\n" +
				"\n" +
				"\t\tint x0 = xx - radius;\n" +
				"\t\tint x1 = x0 + width;\n" +
				"\n" +
				"\t\tint y0 = yy - radius;\n" +
				"\t\tint y1 = y0 + width;\n" +
				"\n" +
				"\t\tif( x0 < 0 ) x0 = 0;\n" +
				"\t\tif( x1 > image.width ) x1 = image.width;\n" +
				"\n" +
				"\t\tif( y0 < 0 ) y0 = 0;\n" +
				"\t\tif( y1 > image.height ) y1 = image.height;\n" +
				"\n" +
				"\t\tfloat value = 0;\n" +
				"\t\tfloat totalWeightY = 0;\n" +
				"\t\tfor( int i = y0; i < y1; i++ ) {\n" +
				"\t\t\tint indexSrc = image.startIndex + i*image.stride + x0;\n" +
				"\t\t\tfloat totalWeightX = 0;\n" +
				"\t\t\tfloat valueX = 0;\n" +
				"\t\t\tfor( int j = x0; j < x1; j++ ) {\n" +
				"\t\t\t\tfloat w = kernel.compute(j-x);\n" +
				"\t\t\t\ttotalWeightX += w;\n" +
				"\t\t\t\tvalueX += w * (image.data[ indexSrc++ ]"+bitWise+");\n" +
				"\t\t\t}\n" +
				"\t\t\tfloat w = kernel.compute(i-y);\n" +
				"\t\t\ttotalWeightY +=  w;\n" +
				"\t\t\tvalue += w*valueX/totalWeightX;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tvalue /= totalWeightY;\n" +
				"\t\t\n" +
				"\t\tif( value > max )\n" +
				"\t\t\treturn max;\n" +
				"\t\telse if( value < min )\n" +
				"\t\t\treturn min;\n" +
				"\t\telse\n" +
				"\t\t\treturn value;\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic float get_unsafe(float x, float y) {\n" +
				"\t\tint xx = (int)x;\n" +
				"\t\tint yy = (int)y;\n" +
				"\n" +
				"\t\tfinal int radius = kernel.getRadius();\n" +
				"\t\tfinal int width = kernel.getWidth();\n" +
				"\n" +
				"\t\tint x0 = xx - radius;\n" +
				"\t\tint x1 = x0 + width;\n" +
				"\n" +
				"\t\tint y0 = yy - radius;\n" +
				"\t\tint y1 = y0 + width;\n" +
				"\n" +
				"\t\tfloat value = 0;\n" +
				"\t\tfor( int i = y0; i < y1; i++ ) {\n" +
				"\t\t\tint indexSrc = image.startIndex + i*image.stride + x0;\n" +
				"\t\t\tfloat valueX = 0;\n" +
				"\t\t\tfor( int j = x0; j < x1; j++ ) {\n" +
				"\t\t\t\tfloat w = kernel.compute(j-x);\n" +
				"\t\t\t\tvalueX += w * (image.data[ indexSrc++ ]"+bitWise+");\n" +
				"\t\t\t}\n" +
				"\t\t\tfloat w = kernel.compute(i-y);\n" +
				"\t\t\tvalue += w*valueX;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tif( value > max )\n" +
				"\t\t\treturn max;\n" +
				"\t\telse if( value < min )\n" +
				"\t\t\treturn min;\n" +
				"\t\telse\n" +
				"\t\t\treturn value;\n"+
				"\t}\n"+
				"\t@Override\n" +
				"\tpublic boolean isInSafeBounds(float x, float y) {\n" +
				"\t\tfloat r = kernel.getRadius();\n" +
				"\t\t\n" +
				"\t\treturn (x-r >= 0 && y-r >= 0 && x+r < image.width && y+r <image.height);\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplInterpolatePixelConvolution app = new GenerateImplInterpolatePixelConvolution();
		app.generate();
	}
}
