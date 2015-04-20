/*
 * Copyright (C) 2014 by Array Systems Computing Inc. http://www.array.ca
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.csa.rstb.gpf;

import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.util.TestData;
import org.esa.snap.util.TestUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit test for PolarimetricSpeckleFilterOp.
 */
public class TestPolarimetricSpeckleFilterOp {

    static {
        TestUtils.initTestEnvironment();
    }

    private final static OperatorSpi spi = new PolarimetricSpeckleFilterOp.Spi();

    private final static String inputPathQuad = TestData.inputSAR + "\\QuadPol\\QuadPol_subset_0_of_RS2-SLC-PDS_00058900.dim";
    private final static String inputQuadFullStack = TestData.inputSAR + "\\QuadPolStack\\RS2-Quad_Pol_Stack.dim";
    private final static String inputC3Stack = TestData.inputSAR + "\\QuadPolStack\\RS2-C3-Stack.dim";
    private final static String inputT3Stack = TestData.inputSAR + "\\QuadPolStack\\RS2-T3-Stack.dim";

    private final static String expectedBoxCar = TestUtils.rootPathTestProducts + "\\expected\\QuadPol\\QuadPol_subset_0_of_RS2-SLC-PDS_00058900_BoxCar.dim";
    private final static String expectedRefinedLee = TestUtils.rootPathTestProducts + "\\expected\\QuadPol\\QuadPol_subset_0_of_RS2-SLC-PDS_00058900_RefinedLee.dim";
    private final static String expectedIDAN = TestUtils.rootPathTestProducts + "\\expected\\QuadPol\\QuadPol_subset_0_of_RS2-SLC-PDS_00058900_IDAN.dim";

    private Product runFilter(final PolarimetricSpeckleFilterOp op,
                              final String filterName, final String path) throws Exception {
        final File inputFile = new File(path);
        if (!inputFile.exists()) {
            TestUtils.skipTest(this, path + " not found");
            return null;
        }
        final Product sourceProduct = TestUtils.readSourceProduct(inputFile);

        assertNotNull(op);
        op.setSourceProduct(sourceProduct);
        op.SetFilter(filterName);

        // get targetProduct: execute initialize()
        final Product targetProduct = op.getTargetProduct();
        TestUtils.verifyProduct(targetProduct, false, false);
        return targetProduct;
    }

    /**
     * Perform Box Car filtering of a Radarsat-2 product and compares it with processed product known to be correct
     *
     * @throws Exception general exception
     */
    @Test
    public void testBoxCarFilter() throws Exception {

        final PolarimetricSpeckleFilterOp op = (PolarimetricSpeckleFilterOp) spi.createOperator();
        final Product targetProduct = runFilter(op, PolarimetricSpeckleFilterOp.BOXCAR_SPECKLE_FILTER, inputPathQuad);
        if (targetProduct != null)
            TestUtils.compareProducts(targetProduct, expectedBoxCar, null);
    }

    /**
     * Perform Refined Lee filtering of a Radarsat-2 product and compares it with processed product known to be correct
     *
     * @throws Exception general exception
     */
    @Test
    public void testRefinedLeeFilter() throws Exception {

        final PolarimetricSpeckleFilterOp op = (PolarimetricSpeckleFilterOp) spi.createOperator();
        final Product targetProduct = runFilter(op, PolarimetricSpeckleFilterOp.REFINED_LEE_FILTER, inputPathQuad);
        if (targetProduct != null)
            TestUtils.compareProducts(targetProduct, expectedRefinedLee, null);
    }

    /**
     * Perform IDAN filtering of a Radarsat-2 product and compares it with processed product known to be correct
     *
     * @throws Exception general exception
     */
    @Test
    public void testIDANFilter() throws Exception {

        final PolarimetricSpeckleFilterOp op = (PolarimetricSpeckleFilterOp) spi.createOperator();
        final Product targetProduct = runFilter(op, PolarimetricSpeckleFilterOp.IDAN_FILTER, inputPathQuad);
        if (targetProduct != null)
            TestUtils.compareProducts(targetProduct, expectedIDAN, null);
    }

    // Stack

    @Test
    public void testBoxCarStack() throws Exception {

        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.BOXCAR_SPECKLE_FILTER, inputPathQuad);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.BOXCAR_SPECKLE_FILTER, inputQuadFullStack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.BOXCAR_SPECKLE_FILTER, inputC3Stack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.BOXCAR_SPECKLE_FILTER, inputT3Stack);
    }

    @Test
    public void testRefinedLeeStack() throws Exception {

        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.REFINED_LEE_FILTER, inputPathQuad);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.REFINED_LEE_FILTER, inputQuadFullStack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.REFINED_LEE_FILTER, inputC3Stack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.REFINED_LEE_FILTER, inputT3Stack);
    }

    @Test
    public void testIDANStack() throws Exception {

        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.IDAN_FILTER, inputPathQuad);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.IDAN_FILTER, inputQuadFullStack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.IDAN_FILTER, inputC3Stack);
        runFilter((PolarimetricSpeckleFilterOp) spi.createOperator(),
                PolarimetricSpeckleFilterOp.IDAN_FILTER, inputT3Stack);
    }
}
