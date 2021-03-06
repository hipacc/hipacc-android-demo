package org.hipacc.demo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.hipacc.demo.R;
import org.hipacc.demo.ScriptC_fsblur;
import org.hipacc.demo.ScriptC_fsgaussian;
import org.hipacc.demo.ScriptC_fsharris;
import org.hipacc.demo.ScriptC_fsharrisderiv;
import org.hipacc.demo.ScriptC_fslaplace;
import org.hipacc.demo.ScriptC_fssobel;
import org.hipacc.demo.ScriptC_rsblur;
import org.hipacc.demo.ScriptC_rsgaussian;
import org.hipacc.demo.ScriptC_rsharris;
import org.hipacc.demo.ScriptC_rsharrisderiv;
import org.hipacc.demo.ScriptC_rslaplace;
import org.hipacc.demo.ScriptC_rssobel;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;

public class NaiveFilters {

    private Context mCtx;
    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;

    public NaiveFilters(Context ctx) {
        mCtx = ctx;
        mRS = RenderScript.create(ctx);
    }

    private boolean init(Bitmap in, Bitmap out, boolean alloc) {
        // Checking dimension
        if (in.getWidth() != out.getWidth() ||
            in.getHeight() != out.getHeight()) {
            return false;
        }

        // Checking format
        if (in.getConfig() != Bitmap.Config.ARGB_8888 ||
            out.getConfig() != Bitmap.Config.ARGB_8888) {
            return false;
        }

        if (alloc) {
            mInAllocation = Allocation.createFromBitmap(mRS, in,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createTyped(mRS,
                    mInAllocation.getType());
        }

        return true;
    }

    public int runRSBlur(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            ScriptC_rsblur script = new ScriptC_rsblur(mRS, mCtx.getResources(),
                    R.raw.rsblur);
            script.set_input(mInAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }
    
    public int runFSBlur(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            ScriptC_fsblur script = new ScriptC_fsblur(mRS, mCtx.getResources(),
                    R.raw.fsblur);
            script.set_input(mInAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    public int runRSGaussian(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.F32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskAllocation.copyFrom(new float[]{
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.026151f, 0.090339f, 0.136565f, 0.090339f, 0.026151f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f
            });

            ScriptC_rsgaussian script = new ScriptC_rsgaussian(mRS,
                    mCtx.getResources(), R.raw.rsgaussian);
            script.set_input(mInAllocation);
            script.set_mask(maskAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }
    
    public int runFSGaussian(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.F32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskAllocation.copyFrom(new float[]{
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.026151f, 0.090339f, 0.136565f, 0.090339f, 0.026151f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f
            });

            ScriptC_fsgaussian script = new ScriptC_fsgaussian(mRS,
                    mCtx.getResources(), R.raw.fsgaussian);
            script.set_input(mInAllocation);
            script.set_mask(maskAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    public int runRSLaplace(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.I32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskAllocation.copyFrom(new int[]{
                    1,   1,   1,   1,   1,
                    1,   1,   1,   1,   1,
                    1,   1, -24,   1,   1,
                    1,   1,   1,   1,   1,
                    1,   1,   1,   1,   1
            });

            ScriptC_rslaplace script = new ScriptC_rslaplace(mRS,
                    mCtx.getResources(), R.raw.rslaplace);
            script.set_input(mInAllocation);
            script.set_mask(maskAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    public int runFSLaplace(Bitmap in, Bitmap out) {
        long time = -1;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.I32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskAllocation.copyFrom(new int[]{
                    1,   1,   1,   1,   1,
                    1,   1,   1,   1,   1,
                    1,   1, -24,   1,   1,
                    1,   1,   1,   1,   1,
                    1,   1,   1,   1,   1
            });

            ScriptC_fslaplace script = new ScriptC_fslaplace(mRS,
                    mCtx.getResources(), R.raw.fslaplace);
            script.set_input(mInAllocation);
            script.set_mask(maskAllocation);
            script.set_width(in.getWidth());
            script.set_height(in.getHeight());

            mRS.finish();
            time = System.nanoTime();
            script.forEach_root(mOutAllocation);

            mRS.finish();
            time = (System.nanoTime() - time) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    public int runRSSobel(Bitmap in, Bitmap out) {
        long time = -1;
        long timeKernel;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.I32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskXAllocation =
                    Allocation.createTyped(mRS, maskType.create());
            Allocation maskYAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskXAllocation.copyFrom(new int[]{
                    -1, -2,  0,  2,  1,
                    -4, -8,  0,  8,  4,
                    -6, -12, 0,  12, 6,
                    -4, -8,  0,  8,  4,
                    -1, -2,  0,  2,  1
            });

            maskYAllocation.copyFrom(new int[]{
                    -1, -4, -6,  -4, -1,
                    -2, -8, -12, -8, -2,
                     0,  0,  0,   0,  0,
                     2,  8,  12,  8,  2,
                     1,  4,  6,   4,  1
            });

            ScriptC_rssobel sobel = new ScriptC_rssobel(mRS,
                    mCtx.getResources(), R.raw.rssobel);
            sobel.set_input(mInAllocation);
            sobel.set_maskX(maskXAllocation);
            sobel.set_maskY(maskYAllocation);
            sobel.set_width(in.getWidth());
            sobel.set_height(in.getHeight());

            mRS.finish();
            timeKernel = System.nanoTime();
            sobel.forEach_root(mOutAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    public int runFSSobel(Bitmap in, Bitmap out) {
        long time = -1;
        long timeKernel;

        if (init(in, out, true)) {
            Type.Builder maskType = new Type.Builder(mRS, Element.I32(mRS));
            maskType.setX(5);
            maskType.setY(5);

            Allocation maskXAllocation =
                    Allocation.createTyped(mRS, maskType.create());
            Allocation maskYAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskXAllocation.copyFrom(new int[]{
                    -1, -2,  0,  2,  1,
                    -4, -8,  0,  8,  4,
                    -6, -12, 0,  12, 6,
                    -4, -8,  0,  8,  4,
                    -1, -2,  0,  2,  1
            });

            maskYAllocation.copyFrom(new int[]{
                    -1, -4, -6,  -4, -1,
                    -2, -8, -12, -8, -2,
                     0,  0,  0,   0,  0,
                     2,  8,  12,  8,  2,
                     1,  4,  6,   4,  1
            });

            ScriptC_fssobel sobel = new ScriptC_fssobel(mRS,
                    mCtx.getResources(), R.raw.fssobel);
            sobel.set_input(mInAllocation);
            sobel.set_maskX(maskXAllocation);
            sobel.set_maskY(maskYAllocation);
            sobel.set_width(in.getWidth());
            sobel.set_height(in.getHeight());

            mRS.finish();
            timeKernel = System.nanoTime();
            sobel.forEach_root(mOutAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            mOutAllocation.copyTo(out);
            mRS.finish();
        }

        return (int)time;
    }

    private float ucharToFloat(byte in) {
        return in < 0 ? (float)in + 256 : in;
    }

    public int runRSHarris(Bitmap in, Bitmap out) {
        long time = -1;
        long timeKernel;

        if (init(in, out, false)) {
            float k = 0.04f;
            float threshold = 20000.0f;

            ByteBuffer bufRGB = ByteBuffer.allocate(in.getWidth() * in.getHeight() * 4);
            FloatBuffer bufGray = FloatBuffer.allocate(in.getWidth() * in.getHeight());

            in.copyPixelsToBuffer(bufRGB);
            
            bufRGB.rewind();

            for (int i = 0; i < in.getHeight() * in.getWidth(); ++i) {
                bufGray.put(i, .2126f * ucharToFloat(bufRGB.get(4*i)) +
                               .7152f * ucharToFloat(bufRGB.get(4*i + 1)) +
                               .0722f * ucharToFloat(bufRGB.get(4*i + 2)));
            }

            Type.Builder derivType = new Type.Builder(mRS, Element.F32(mRS));
            derivType.setX(in.getWidth());
            derivType.setY(in.getHeight());

            Type.Builder maskType = new Type.Builder(mRS, Element.F32(mRS));
            maskType.setX(3);
            maskType.setY(3);

            Allocation grayAllocation =
                    Allocation.createTyped(mRS, derivType.create());

            Allocation derivXAllocation =
                    Allocation.createTyped(mRS, derivType.create());
            Allocation derivYAllocation =
                    Allocation.createTyped(mRS, derivType.create());

            Allocation maskDerivXAllocation =
                    Allocation.createTyped(mRS, maskType.create());
            Allocation maskDerivYAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskDerivXAllocation.copyFrom(new float[]{
                    -0.166666667f,          0.0f,  0.166666667f,
                    -0.166666667f,          0.0f,  0.166666667f,
                    -0.166666667f,          0.0f,  0.166666667f
            });
            
            maskDerivYAllocation.copyFrom(new float[]{
                    -0.166666667f, -0.166666667f, -0.166666667f,
                             0.0f,          0.0f,          0.0f,
                     0.166666667f,  0.166666667f,  0.166666667f
            });

            maskType.setX(5);
            maskType.setY(5);

            Allocation maskGaussAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskGaussAllocation.copyFrom(new float[]{
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.026151f, 0.090339f, 0.136565f, 0.090339f, 0.026151f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f
            });

            grayAllocation.copyFrom(bufGray.array());

            ScriptC_rsharrisderiv deriv = new ScriptC_rsharrisderiv(mRS,
                    mCtx.getResources(), R.raw.rsharrisderiv);
            deriv.set_input(grayAllocation);
            deriv.set_width(in.getWidth());
            deriv.set_height(in.getHeight());

            ScriptC_rsharris harris = new ScriptC_rsharris(mRS,
                    mCtx.getResources(), R.raw.rsharris);
            harris.set_derivX(derivXAllocation);
            harris.set_derivY(derivYAllocation);
            harris.set_mask(maskGaussAllocation);
            harris.set_width(in.getWidth());
            harris.set_height(in.getHeight());
            harris.set_k(k);

            // First run
            deriv.set_mask(maskDerivXAllocation);

            mRS.finish();
            timeKernel = System.nanoTime();
            deriv.forEach_root(derivXAllocation);

            mRS.finish();
            time = (System.nanoTime() - timeKernel) / 1000000;
            
            // Second run
            deriv.set_mask(maskDerivYAllocation);

            mRS.finish();
            timeKernel = System.nanoTime();
            deriv.forEach_root(derivYAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            // Third run
            mRS.finish();
            timeKernel = System.nanoTime();
            harris.forEach_root(grayAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            grayAllocation.copyTo(bufGray.array());

            // Draw output
            for (int x = 0; x < in.getWidth(); ++x) {
                for (int y = 0; y < in.getHeight(); y++) {
                    int pos = y*in.getWidth() + x;
                    if (bufGray.get(pos) > threshold) {
                        for (int i = -10; i <= 10; ++i) {
                            if (x+i >= 0 && x+i < in.getWidth()) {
                                bufRGB.put((pos + i)*4, (byte)255);
                                bufRGB.put((pos + i)*4 + 1, (byte)255);
                                bufRGB.put((pos + i)*4 + 2, (byte)255);
                            }
                        }
                        for (int i = -10; i <= 10; ++i) {
                            if (y+i >= 0 && y+i < in.getHeight()) {
                                bufRGB.put((pos + i*in.getWidth())*4, (byte)255);
                                bufRGB.put((pos + i*in.getWidth())*4 + 1, (byte)255);
                                bufRGB.put((pos + i*in.getWidth())*4 + 2, (byte)255);
                            }
                        }
                    }
                }
            }

            out.copyPixelsFromBuffer(bufRGB);
        }

        return (int)time;
    }

    public int runFSHarris(Bitmap in, Bitmap out) {
        long time = -1;
        long timeKernel;

        if (init(in, out, false)) {
            float k = 0.04f;
            float threshold = 20000.0f;

            ByteBuffer bufRGB = ByteBuffer.allocate(in.getWidth() * in.getHeight() * 4);
            FloatBuffer bufGray = FloatBuffer.allocate(in.getWidth() * in.getHeight());

            in.copyPixelsToBuffer(bufRGB);
            
            bufRGB.rewind();

            for (int i = 0; i < in.getHeight() * in.getWidth(); ++i) {
                bufGray.put(i, .2126f * ucharToFloat(bufRGB.get(4*i)) +
                               .7152f * ucharToFloat(bufRGB.get(4*i + 1)) +
                               .0722f * ucharToFloat(bufRGB.get(4*i + 2)));
            }

            Type.Builder derivType = new Type.Builder(mRS, Element.F32(mRS));
            derivType.setX(in.getWidth());
            derivType.setY(in.getHeight());

            Type.Builder maskType = new Type.Builder(mRS, Element.F32(mRS));
            maskType.setX(3);
            maskType.setY(3);

            Allocation grayAllocation =
                    Allocation.createTyped(mRS, derivType.create());

            Allocation derivXAllocation =
                    Allocation.createTyped(mRS, derivType.create());
            Allocation derivYAllocation =
                    Allocation.createTyped(mRS, derivType.create());

            Allocation maskDerivXAllocation =
                    Allocation.createTyped(mRS, maskType.create());
            Allocation maskDerivYAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskDerivXAllocation.copyFrom(new float[]{
                    -0.166666667f,          0.0f,  0.166666667f,
                    -0.166666667f,          0.0f,  0.166666667f,
                    -0.166666667f,          0.0f,  0.166666667f
            });
            
            maskDerivYAllocation.copyFrom(new float[]{
                    -0.166666667f, -0.166666667f, -0.166666667f,
                             0.0f,          0.0f,          0.0f,
                     0.166666667f,  0.166666667f,  0.166666667f
            });

            maskType.setX(5);
            maskType.setY(5);

            Allocation maskGaussAllocation =
                    Allocation.createTyped(mRS, maskType.create());

            maskGaussAllocation.copyFrom(new float[]{
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.026151f, 0.090339f, 0.136565f, 0.090339f, 0.026151f,
                    0.017300f, 0.059761f, 0.090339f, 0.059761f, 0.017300f,
                    0.005008f, 0.017300f, 0.026151f, 0.017300f, 0.005008f
            });

            grayAllocation.copyFrom(bufGray.array());

            ScriptC_fsharrisderiv deriv = new ScriptC_fsharrisderiv(mRS,
                    mCtx.getResources(), R.raw.fsharrisderiv);
            deriv.set_input(grayAllocation);
            deriv.set_width(in.getWidth());
            deriv.set_height(in.getHeight());

            ScriptC_fsharris harris = new ScriptC_fsharris(mRS,
                    mCtx.getResources(), R.raw.fsharris);
            harris.set_derivX(derivXAllocation);
            harris.set_derivY(derivYAllocation);
            harris.set_mask(maskGaussAllocation);
            harris.set_width(in.getWidth());
            harris.set_height(in.getHeight());
            harris.set_k(k);

            // First run
            deriv.set_mask(maskDerivXAllocation);

            mRS.finish();
            timeKernel = System.nanoTime();
            deriv.forEach_root(derivXAllocation);

            mRS.finish();
            time = (System.nanoTime() - timeKernel) / 1000000;
            
            // Second run
            deriv.set_mask(maskDerivYAllocation);

            mRS.finish();
            timeKernel = System.nanoTime();
            deriv.forEach_root(derivYAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            // Third run
            mRS.finish();
            timeKernel = System.nanoTime();
            harris.forEach_root(grayAllocation);

            mRS.finish();
            time += (System.nanoTime() - timeKernel) / 1000000;

            grayAllocation.copyTo(bufGray.array());

            // Draw output
            for (int x = 0; x < in.getWidth(); ++x) {
                for (int y = 0; y < in.getHeight(); y++) {
                    int pos = y*in.getWidth() + x;
                    if (bufGray.get(pos) > threshold) {
                        for (int i = -10; i <= 10; ++i) {
                            if (x+i >= 0 && x+i < in.getWidth()) {
                                bufRGB.put((pos + i)*4, (byte)255);
                                bufRGB.put((pos + i)*4 + 1, (byte)255);
                                bufRGB.put((pos + i)*4 + 2, (byte)255);
                            }
                        }
                        for (int i = -10; i <= 10; ++i) {
                            if (y+i >= 0 && y+i < in.getHeight()) {
                                bufRGB.put((pos + i*in.getWidth())*4, (byte)255);
                                bufRGB.put((pos + i*in.getWidth())*4 + 1, (byte)255);
                                bufRGB.put((pos + i*in.getWidth())*4 + 2, (byte)255);
                            }
                        }
                    }
                }
            }

            out.copyPixelsFromBuffer(bufRGB);
        }

        return (int)time;
    }
}
