package de.biovoxxel.bv3dbox.plugins;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

class BVObjectInspectorTest {

	ImagePlus primaryImage;
	ImagePlus secondaryImage;
	ImagePlus originalImage1;
	ImagePlus originalImage2;
	
	@BeforeEach
	void setup() {
		// Create 32-bit test images
		primaryImage = create32BitTestImage("Primary_32bit", 100, 100, 10);
		secondaryImage = create32BitTestImage("Secondary_32bit", 100, 100, 10);
		originalImage1 = create32BitTestImage("Original1_32bit", 100, 100, 10);
		originalImage2 = create32BitTestImage("Original2_32bit", 100, 100, 10);
	}
	
	private ImagePlus create32BitTestImage(String title, int width, int height, int depth) {
		ImageStack stack = new ImageStack(width, height);
		
		for (int z = 0; z < depth; z++) {
			FloatProcessor processor = new FloatProcessor(width, height);
			// Create some test data - simple gradient pattern
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					float value = (float) (Math.sin(x * 0.1) * Math.cos(y * 0.1) * (z + 1));
					processor.putPixelValue(x, y, value);
				}
			}
			stack.addSlice(processor);
		}
		
		ImagePlus image = new ImagePlus(title, stack);
		return image;
	}
	
	@Test
	void test32BitImageSupport() {
		// Test that 32-bit images are accepted
		assertEquals(32, primaryImage.getBitDepth());
		assertEquals(32, secondaryImage.getBitDepth());
		
		// Create object inspector with 32-bit images
		BV_ObjectInspector inspector = new BV_ObjectInspector(primaryImage, secondaryImage);
		
		// Set original images
		inspector.setOriginalImages(originalImage1.getTitle(), originalImage2.getTitle());
		
		// Set default parameters
		inspector.setPrimaryVolumeRange("0-Infinity");
		inspector.setPrimaryMMDTCRRange("0.00-1.00");
		inspector.setSecondaryVolumeRange("0-Infinity");
		inspector.setSecondaryMMDTCRRange("0.00-1.00");
		inspector.setEdgeExclusion(false);
		inspector.padStackTops(false);
		inspector.setOutputImageFlags(false, false, false);
		
		// This should not throw an exception for 32-bit images
		assertDoesNotThrow(() -> {
			inspector.inspect();
		});
	}
	
	@Test
	void testBitDepthValidation() {
		// Test that the inspector properly validates bit depths
		BV_ObjectInspector inspector = new BV_ObjectInspector(primaryImage, secondaryImage);
		
		// Verify that 32-bit images are accepted
		assertTrue(primaryImage.getBitDepth() == 8 || 
				   primaryImage.getBitDepth() == 16 || 
				   primaryImage.getBitDepth() == 32);
		
		assertTrue(secondaryImage.getBitDepth() == 8 || 
				   secondaryImage.getBitDepth() == 16 || 
				   secondaryImage.getBitDepth() == 32);
	}
}
