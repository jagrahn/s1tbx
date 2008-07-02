/*
 * $id$
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package org.esa.beam.framework.datamodel;

import com.bc.ceres.core.Assert;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.math.Histogram;
import org.esa.beam.util.math.MathUtils;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

/**
 * This class contains information about how a product's raster data node is displayed as an image.
 *
 * @author Norman Fomferra
 * @version $Revision$ $Date$
 */
public class ImageInfo implements Cloneable {

    public static final Color NO_COLOR = new Color(0, 0, 0, 0);

    public static final String HISTOGRAM_MATCHING_OFF = "off";
    public static final String HISTOGRAM_MATCHING_EQUALIZE = "equalize";
    public static final String HISTOGRAM_MATCHING_NORMALIZE = "normalize";

    private ColorPaletteDef colorPaletteDef;
    // todo - save in DIMAP   (nf/mp - 26.06.2008)
    private RGBChannelDef rgbChannelDef;
    // todo - save in DIMAP   (nf/mp - 26.06.2008)
    private Color noDataColor;
    // todo - include in module and DIMAP XML (nf/mp - 26.06.2008)
    // todo - use enum (nf/mp - 26.06.2008)
    private String histogramMatching;

    //todo - this GUI code, move this elsewhere (nf/mp - 26.06.2008)
    // Color palette view properties.
    // Used by ContrastStretchPane, properties currently not saved in DIMAP.
    @Deprecated
    private Float histogramViewGain;
    @Deprecated
    private Float minHistogramViewSample;
    @Deprecated
    private Float maxHistogramViewSample;

    // todo - move to RasterDataNode.Statistics (nf/mp - 26.06.2008)
    @Deprecated
    private float minSample;
    @Deprecated
    private float maxSample;
    @Deprecated
    private int[] histogramBins; // raw data!

    /**
     * Constructs a new image information instance.
     *
     * @param colorPaletteDef the color palette definition
     */
    public ImageInfo(ColorPaletteDef colorPaletteDef) {
        Assert.notNull(colorPaletteDef, "colorPaletteDef");
        this.colorPaletteDef = colorPaletteDef;
        this.rgbChannelDef = null;
        this.noDataColor = NO_COLOR;
        this.histogramMatching = ImageInfo.HISTOGRAM_MATCHING_OFF;
    }

    /**
     * Constructs a new RGB image information instance.
     *
     * @param rgbChannelDef the RGB channel definition
     */
    public ImageInfo(RGBChannelDef rgbChannelDef) {
        Assert.notNull(rgbChannelDef, "rgbChannelDef");
        this.colorPaletteDef = null;
        this.rgbChannelDef = rgbChannelDef;
        this.noDataColor = NO_COLOR;
        this.histogramMatching = ImageInfo.HISTOGRAM_MATCHING_OFF;
    }


    /**
     * Constructs a new basic display information instance.
     *
     * @param minSample     the statistical minimum sample value
     * @param maxSample     the statistical maximum sample value
     * @param histogramBins the histogram pixel counts, can be <code>null</code>
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public ImageInfo(float minSample,
                     float maxSample,
                     int[] histogramBins) {
        this(minSample,
             maxSample,
             histogramBins,
             new ColorPaletteDef(256, minSample, maxSample));
    }

    // todo - remove from BEAM code
    /**
     * Constructs a new basic display information instance.
     *
     * @param minSample          the statistical minimum sample value
     * @param maxSample          the statistical maximum sample value
     * @param histogramBins      the histogram pixel counts, can be <code>null</code>
     * @param numColors          the number of colors for the color palette
     * @param colorPalettePoints the points of the gradation curve
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public ImageInfo(float minSample,
                     float maxSample,
                     int[] histogramBins,
                     int numColors,
                     ColorPaletteDef.Point[] colorPalettePoints) {
        this(minSample,
             maxSample,
             histogramBins,
             new ColorPaletteDef(colorPalettePoints, numColors));
    }

    // todo - remove from BEAM code
    /**
     * Constructs a new basic display information instance.
     *
     * @param minSample       the statistical minimum sample value
     * @param maxSample       the statistical maximum sample value
     * @param histogramBins   the histogram pixel counts, can be <code>null</code>
     * @param numColors       the number of colors for the color palette
     * @param colorPaletteDef the color palette definition
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public ImageInfo(float minSample,
                     float maxSample,
                     int[] histogramBins,
                     int numColors,
                     ColorPaletteDef colorPaletteDef) {
        this(minSample, maxSample, histogramBins, colorPaletteDef);
        this.colorPaletteDef.setNumColors(numColors);
    }

    // todo - remove from BEAM code
    /**
     * Constructs a new basic display information instance.
     *
     * @param minSample       the statistical minimum sample value
     * @param maxSample       the statistical maximum sample value
     * @param histogramBins   the histogram pixel counts, can be <code>null</code>
     * @param colorPaletteDef the color palette definition
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public ImageInfo(float minSample,
                     float maxSample,
                     int[] histogramBins,
                     ColorPaletteDef colorPaletteDef) {
        Guardian.assertNotNull("colorPaletteDef", colorPaletteDef);
        this.rgbChannelDef = null;
        this.colorPaletteDef = colorPaletteDef;
        this.noDataColor = null;
        this.histogramMatching = ImageInfo.HISTOGRAM_MATCHING_OFF;

        this.minSample = minSample;
        this.maxSample = maxSample;
        this.histogramBins = histogramBins;
    }

    /**
     * Gets the color palette definition as used for images created from single bands.
     *
     * @return The color palette definition. Can be {@code null}.
     *         In this case {@link #getRgbChannelDef()} is non-null.
     */
    public ColorPaletteDef getColorPaletteDef() {
        return colorPaletteDef;
    }

    /**
     * Gets the RGB(A) channel definition as used for images created from 3 tp 4 bands.
     *
     * @return The RGB(A) channel definition.
     *         Can be {@code null}. In this case {@link #getColorPaletteDef()} is non-null.
     */
    public RGBChannelDef getRgbChannelDef() {
        return rgbChannelDef;
    }

    public Color getNoDataColor() {
        return noDataColor;
    }

    public void setNoDataColor(Color noDataColor) {
        Assert.notNull(noDataColor, "noDataColor");
        this.noDataColor = noDataColor;
    }

    public String getHistogramMatching() {
        return histogramMatching;
    }

    public void setHistogramMatching(String histogramMatching) {
        Assert.notNull(histogramMatching, "histogramMatching");
        this.histogramMatching = histogramMatching;
    }

    public Color[] getColors() {
        return colorPaletteDef != null ? colorPaletteDef.getColors() : new Color[0];
    }

    /**
     * Gets the number of color components the image shall have using an instance of this {@code ImageInfo}.
     *
     * @return {@code 3} for RGB images, {@code 4} for RGB images with an alpha channel (transparency)
     */
    public int getColorComponentCount() {
        if (noDataColor.getAlpha() < 255) {
            return 4;
        }
        if (colorPaletteDef != null) {
            final Color[] colors = colorPaletteDef.getColors();
            for (Color color : colors) {
                if (color.getAlpha() < 255) {
                    return 4;
                }
            }
        }
        if (rgbChannelDef != null) {
            if (rgbChannelDef.isAlphaUsed()) {
                return 4;
            }
        }
        return 3;
    }

    public IndexColorModel createIndexColorModel(Scaling scaling) {
        if (colorPaletteDef == null) {
            return null;
        }
        Color[] palette = colorPaletteDef.createColorPalette(scaling);
        final int numColors = palette.length;
        final byte[] red = new byte[numColors];
        final byte[] green = new byte[numColors];
        final byte[] blue = new byte[numColors];
        for (int i = 0; i < palette.length; i++) {
            Color color = palette[i];
            red[i] = (byte) color.getRed();
            green[i] = (byte) color.getGreen();
            blue[i] = (byte) color.getBlue();
        }
        return new IndexColorModel(numColors <= 256 ? 8 : 16, numColors, red, green, blue);
    }

    public ComponentColorModel createComponentColorModel() {
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ComponentColorModel cm;
        if (getColorComponentCount() == 4) {
            cm = new ComponentColorModel(cs,
                                         true, // hasAlpha,
                                         false, //isAlphaPremultiplied,
                                         Transparency.TRANSLUCENT, //  transparency,
                                         DataBuffer.TYPE_BYTE); //transferType
        } else {
            cm = new ComponentColorModel(cs,
                                         false, // hasAlpha,
                                         false, //isAlphaPremultiplied,
                                         Transparency.OPAQUE, //  transparency,
                                         DataBuffer.TYPE_BYTE); //transferType

        }
        return cm;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a copy of this object
     */
    @Override
    public final Object clone() {
        try {
            ImageInfo imageInfo = (ImageInfo) super.clone();
            if (colorPaletteDef != null) {
                imageInfo.colorPaletteDef = (ColorPaletteDef) colorPaletteDef.clone();
            }
            if (rgbChannelDef != null) {
                imageInfo.rgbChannelDef = (RGBChannelDef) rgbChannelDef.clone();
            }
            return imageInfo;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns a "deep" copy of this object. The method simply returns the value of
     * {@link #clone()}.
     *
     * @return a copy of this object
     */
    public ImageInfo createDeepCopy() {
        return (ImageInfo) clone();
    }

    /**
     * Releases all of the resources used by this object instance and all of its owned children. Its primary use is to
     * allow the garbage collector to perform a vanilla job.
     * <p/>
     * <p>This method should be called only if it is for sure that this object instance will never be used again. The
     * results of referencing an instance of this class after a call to <code>dispose()</code> are undefined.
     * <p/>
     * <p>Overrides of this method should always call <code>super.dispose();</code> after disposing this instance.
     */
    public void dispose() {
        histogramBins = null;
        if (colorPaletteDef != null) {
            colorPaletteDef.dispose();
        }
        colorPaletteDef = null;
        rgbChannelDef = null;
    }

    public void transferColorPaletteDef(final ImageInfo sourceImageInfo, boolean changeColorsOnly) {
        transferColorPaletteDef(sourceImageInfo.getColorPaletteDef(), changeColorsOnly);
    }

    public void transferColorPaletteDef(final ColorPaletteDef sourceCPD, boolean changeColorsOnly) {
        final ColorPaletteDef currentCPD = getColorPaletteDef();
        int deltaNumPoints = currentCPD.getNumPoints() - sourceCPD.getNumPoints();
        if (deltaNumPoints < 0) {
            for (; deltaNumPoints != 0; deltaNumPoints++) {
                currentCPD.insertPointAfter(0, new ColorPaletteDef.Point());
            }
        } else if (deltaNumPoints > 0) {
            for (; deltaNumPoints != 0; deltaNumPoints--) {
                currentCPD.removePointAt(1);
            }
        }
        if (changeColorsOnly) {
            for (int i = 0; i < sourceCPD.getNumPoints(); i++) {
                currentCPD.getPointAt(i).setColor(sourceCPD.getPointAt(i).getColor());
            }
        } else {
            double min1 = currentCPD.getFirstPoint().getSample();
            double max1 = currentCPD.getLastPoint().getSample();
            double min2 = sourceCPD.getFirstPoint().getSample();
            double max2 = sourceCPD.getLastPoint().getSample();
            double a, b;
            // Check if source range fits into this range
            if (min2 >= min1 && max2 <= max1) {
                // --> ok, no sample conversion
                a = 0.0;
                b = 1.0;
            } else {
                // --> sourcerange overlaps this range, sample conversion
                min1 = currentCPD.getFirstPoint().getSample();
                max1 = currentCPD.getLastPoint().getSample();
                double delta1 = (max1 > min1) ? max1 - min1 : 1;
                double delta2 = (max2 > min2) ? max2 - min2 : 1;
                a = min1 - min2 * delta1 / delta2;
                b = delta1 / delta2;
            }
            for (int i = 0; i < sourceCPD.getNumPoints(); i++) {
                currentCPD.getPointAt(i).setSample(a + b * sourceCPD.getPointAt(i).getSample());
                currentCPD.getPointAt(i).setColor(sourceCPD.getPointAt(i).getColor());
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // DEPRECATED API!!!


    /**
     * Gets the histogram.
     *
     * @return the histogram, or <code>null</code> if a histogram is not available.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public Histogram getHistogram() {
        return isHistogramAvailable() ? new Histogram(histogramBins, Scaling.IDENTITY.scaleInverse((double) minSample),
                                                      Scaling.IDENTITY.scaleInverse((double) maxSample)) : null;
    }

    /**
     * Gets a suitable round factor for the given number of digits.
     *
     * @param numDigits the number of digits.
     * @return a suitable round factor
     * @see org.esa.beam.util.math.MathUtils#computeRoundFactor(double, double, int)
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public double getRoundFactor(int numDigits) {
        return MathUtils.computeRoundFactor(getMinSample(), getMaxSample(), numDigits);
    }

    /**
     * @deprecated since BEAM 4.2, scaling is only used for statistical information which is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public Scaling getScaling() {
        return Scaling.IDENTITY;
    }

    /**
     * @deprecated since BEAM 4.2, scaling is only used for statistical information which is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public void setScaling(Scaling scaling) {
    }


    /**
     * @return the minimum sample value.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public float getMinSample() {
        return minSample;
    }

    /**
     * Sets the minimum sample value.
     *
     * @param minSample the minimum sample value.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public void setMinSample(float minSample) {
        this.minSample = minSample;
    }

    /**
     * @return the maximum sample value.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public float getMaxSample() {
        return maxSample;
    }


    /**
     * Sets the maximum sample value.
     *
     * @param maxSample the maximum sample value.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public void setMaxSample(float maxSample) {
        this.maxSample = maxSample;
    }

    /**
     * Gets the minimum display sample value for a linear contrast stretch operation.
     *
     * @return the minimum display sample
     * @deprecated since BEAM 4.2, use {@link ColorPaletteDef#getFirstPoint()}
     */
    @Deprecated
    public double getMinDisplaySample() {
        Debug.assertNotNull(getColorPaletteDef());
        Debug.assertTrue(getColorPaletteDef().getNumPoints() >= 2);
        return getColorPaletteDef().getFirstPoint().getSample();
    }

    /**
     * Gets the maximum display sample value for a linear contrast stretch operation.
     *
     * @return the maximum display sample
     * @deprecated since BEAM 4.2, use {@link ColorPaletteDef#getLastPoint()}
     */
    @Deprecated
    public double getMaxDisplaySample() {
        Debug.assertNotNull(getColorPaletteDef());
        Debug.assertTrue(getColorPaletteDef().getNumPoints() >= 2);
        return getColorPaletteDef().getLastPoint().getSample();
    }

    /**
     * Gets the minimum sample value used for a histogram view.
     *
     * @return the minimum histogram view sample
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public float getMinHistogramViewSample() {
        if (minHistogramViewSample != null) {
            return minHistogramViewSample;
        }
        return getMinSample();
    }

    /**
     * Sets the minimum sample value used for a histogram view.
     *
     * @param minViewSample the minimum histogram view sample
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public void setMinHistogramViewSample(float minViewSample) {
        minHistogramViewSample = minViewSample;
    }

    /**
     * Gets the maximum sample value used for a histogram view.
     *
     * @return the maximum histogram view sample
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public float getMaxHistogramViewSample() {
        if (maxHistogramViewSample != null) {
            return maxHistogramViewSample;
        }
        return getMaxSample();
    }

    /**
     * Sets the maximum sample value used for a histogram view.
     *
     * @param maxViewSample the maximum histogram view sample
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public void setMaxHistogramViewSample(float maxViewSample) {
        maxHistogramViewSample = maxViewSample;
    }

    /**
     * Gets the gain (Y-axis scale factor) used for a histogram view.
     *
     * @return the histogram view gain
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public float getHistogramViewGain() {
        if (histogramViewGain != null) {
            return histogramViewGain;
        }
        return 1.0f;
    }

    /**
     * Sets the maximum sample value used for a histogram view.
     *
     * @param gain the histogram view gain
     * @deprecated since BEAM 4.2, no replacement, this is GUI related
     */
    @Deprecated
    public void setHistogramViewGain(float gain) {
        histogramViewGain = gain;
    }

    /**
     * @deprecated since BEAM 4.2, use {@link org.esa.beam.framework.datamodel.RGBImageProfile#isGammaActive(int)}
     */
    @Deprecated
    public boolean isGammaActive() {
        return false;
    }

    /**
     * @deprecated since BEAM 4.2, use {@link org.esa.beam.framework.datamodel.RGBImageProfile#getGamma(int)}
     */
    @Deprecated
    public float getGamma() {
        return 1.0f;
    }

    /**
     * @deprecated since BEAM 4.2, use {@link org.esa.beam.framework.datamodel.RGBImageProfile#setGamma(int, double)}
     */
    @Deprecated
    public void setGamma(float gamma) {
    }

    /**
     * Gets the histogram pixel counts.
     *
     * @return the histogram pixel counts, can be <code>null</code> if not available
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public int[] getHistogramBins() {
        return histogramBins;
    }

    /**
     * Sets the histogram pixel counts.
     *
     * @param histogramBins the histogram pixel counts, may be <code>null</code> to signal inavailibility
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public void setHistogramBins(int[] histogramBins) {
        this.histogramBins = histogramBins;
    }


    /**
     * Gets whether or not a histogram is available.
     *
     * @return <code>true</code> if so
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public boolean isHistogramAvailable() {
        return histogramBins != null && histogramBins.length > 0;
    }

    /**
     * Gets the number of bins which are visible in the histogram view.
     *
     * @return the number of bins which are visible, <code>-1</code> if a histogram is not available.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    @Deprecated
    public float getHistogramViewBinCount() {
        if (!isHistogramAvailable()) {
            return -1;
        }
        if (getMinSample() != getMinHistogramViewSample() || getMaxSample() != getMaxHistogramViewSample()) {
            return (float)
                    (getHistogramBins().length
                            / (Scaling.IDENTITY.scaleInverse((double) getMaxSample()) - Scaling.IDENTITY.scaleInverse(
                            (double) getMinSample()))
                            * (Scaling.IDENTITY.scaleInverse((double) getMaxHistogramViewSample()) - Scaling.IDENTITY.scaleInverse(
                            (double) getMinHistogramViewSample()))
                    );
        }
        return getHistogramBins().length;
    }

    /**
     * Returns the float offset in the bins array for the first bin which are visible in the histogram view.
     *
     * @return the float offset in the bins array, <code>-1</code> if a histogram is not available.
     * @deprecated since BEAM 4.2, statistical information is now available via {@link RasterDataNode#getStx()}
     */
    public float getFirstHistogramViewBinIndex() {
        if (!isHistogramAvailable()) {
            return -1;
        }
        if (getMinSample() != getMinHistogramViewSample()) {
            return (float) ((getHistogramBins().length - 1)
                    / (Scaling.IDENTITY.scaleInverse((double) getMaxSample()) - Scaling.IDENTITY.scaleInverse(
                    (double) getMinSample()))
                    * (Scaling.IDENTITY.scaleInverse((double) getMinHistogramViewSample()) - Scaling.IDENTITY.scaleInverse(
                    (double) getMinSample())));
        }
        return 0;
    }

    /**
     * @return the number of colors used to compute the color palette.
     * @deprecated since BEAM 4.2, use {link #getColorPaletteDef}
     */
    @Deprecated
    public int getNumColors() {
        return colorPaletteDef.getNumColors();
    }

    /**
     * @return the color palette. If no such exists a new one is computed from the gradation curve.
     * @deprecated since BEAM 4.2, use {link #createColourPalette()}
     */
    @Deprecated
    public Color[] getColorPalette() {
        return colorPaletteDef.createColorPalette(Scaling.IDENTITY);
    }

    /**
     * @deprecated since BEAM 4.2, use {link #createColourPalette()}
     */
    @Deprecated
    public void computeColorPalette() {
    }

    @Deprecated
    public void setColorPaletteDef(ColorPaletteDef cpd) {
    }

    /**
     * @deprecated since BEAM 4.2, GUI code
     */
    @Deprecated
    public double getNormalizedHistogramViewSampleValue(double sample) {
        return sample;
    }

    /**
     * @deprecated since BEAM 4.2, GUI code
     */
    @Deprecated
    public double getNormalizedDisplaySampleValue(double sample) {
        final double minDisplaySample = Scaling.IDENTITY.scaleInverse(getColorPaletteDef().getFirstPoint().getSample());
        final double maxDisplaySample = Scaling.IDENTITY.scaleInverse(getColorPaletteDef().getLastPoint().getSample());
        sample = Scaling.IDENTITY.scaleInverse(sample);
        double delta = maxDisplaySample - minDisplaySample;
        if (delta == 0 || Double.isNaN(delta)) {
            delta = 1;
        }
        return (sample - minDisplaySample) / delta;
    }

    /**
     * (Re-)Computes the color palette for this basic display information instance.
     *
     * @return the color palette
     * @deprecated since BEAM 4.2, use {@link #getColorPaletteDef()}.{@link ColorPaletteDef#createColorPalette(Scaling) createColorPalette(Scaling)}
     */
    @Deprecated
    public Color[] createColorPalette() {
        return colorPaletteDef != null ? colorPaletteDef.createColorPalette(Scaling.IDENTITY) : new Color[0];
    }

    /**
     * (Re-)Computes the color palette for this basic display information instance.
     *
     * @return the color model
     * @deprecated since BEAM 4.2, use {@link #getColorPaletteDef()}.{@link ColorPaletteDef#createColorPalette(Scaling) createColorPalette(Scaling)}
     */
    @Deprecated
    public IndexColorModel createColorModel() {
        return createIndexColorModel(Scaling.IDENTITY);
    }

}
