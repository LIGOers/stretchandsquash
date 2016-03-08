//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.concurrent.CountDownLatch;
import org.gwoptics.mathutils.TrigLookup;
import processing.core.PConstants;
import processing.core.PImage;

public class ImageWorker implements Runnable, PConstants {
    private int _start;
    private int _end;
    private Object _processingLock = new Object();
    static boolean interpolate = true;
    public float time;
    public float h_plus_amp;
    public float h_cross_amp;
    public float frequency;
    public float h_plus_phs;
    public float h_cross_phs;
    private PImage _outImg;
    private PImage _inImg;
    private int _imgOffsetX;
    private int _imgOffsetY;
    private int _inImgOffsetX;
    private int _inImgOffsetY;
    private CountDownLatch _stillRunning;

    public ImageWorker() {
    }

    public void setStartIndex(int var1) {
        this._start = var1;
    }

    public void setEndIndex(int var1) {
        this._end = var1;
    }

    public void setOutputImage(PImage var1) {
        Object var2 = this._processingLock;
        synchronized(this._processingLock) {
            this._outImg = var1;
            this._imgOffsetX = Math.round((float)this._outImg.width * 0.5F);
            this._imgOffsetY = Math.round((float)this._outImg.height * 0.5F);
        }
    }

    public void setCountDownLatch(CountDownLatch var1) {
        Object var2 = this._processingLock;
        synchronized(this._processingLock) {
            this._stillRunning = var1;
        }
    }

    public void setInputImage(PImage var1) {
        Object var2 = this._processingLock;
        synchronized(this._processingLock) {
            if(var1 == null) {
                throw new NullPointerException("Input image nulled");
            } else {
                this._inImg = var1;
                this._inImgOffsetX = Math.round((float)this._inImg.width * 0.5F);
                this._inImgOffsetY = Math.round((float)this._inImg.height * 0.5F);
            }
        }
    }

    public void run() {
        Object var1 = this._processingLock;
        synchronized(this._processingLock) {
            if(this._inImg != null) {
                float var2 = 6.2831855F * this.frequency * this.time;
                float var3 = var2 + 3.1415927F;
                double var4 = 0.5D + (double)this.h_cross_amp * TrigLookup.sin(var2 + this.h_cross_phs);
                double var6 = 0.5D + (double)this.h_cross_amp * TrigLookup.sin(var3 + this.h_cross_phs);
                double var8 = 0.5D + (double)this.h_plus_amp * TrigLookup.sin(var2 + this.h_plus_phs);
                double var10 = 0.5D + (double)this.h_plus_amp * TrigLookup.sin(var3 + this.h_plus_phs);
                double var12 = (var4 + var6) * 0.5D;
                double var14 = (var4 - var6) * 0.5D;
                double var16 = var10 + var12;
                double var18 = var8 + var12;

                for(int var22 = this._start; var22 < this._end; ++var22) {
                    int var21 = var22 / this._outImg.width;
                    int var20 = var22 - var21 * this._outImg.width;
                    var20 -= this._imgOffsetX;
                    var21 -= this._imgOffsetY;
                    float var23 = (float)((double)var20 * var16 + (double)var21 * var14) + (float)this._inImgOffsetX;
                    float var24 = (float)((double)var20 * var14 + (double)var21 * var18) + (float)this._inImgOffsetY;
                    int var25 = (int)var23;
                    int var26 = (int)var24;
                    int var27 = var26 * this._inImg.width + var25;
                    if(interpolate) {
                        int var28 = var27 + 1;
                        int var29 = var27 + this._inImg.width;
                        int var30 = var29 + 1;
                        if(var27 >= 0 && var30 < this._inImg.pixels.length) {
                            float var31 = var23 - (float)var25;
                            float var32 = var24 - (float)var26;
                            int var33 = this._inImg.pixels[var27];
                            int var34 = this._inImg.pixels[var28];
                            int var35 = this._inImg.pixels[var29];
                            int var36 = this._inImg.pixels[var30];
                            this._outImg.pixels[var22] = this.mylerp(var33, var34, var35, var36, var31, var32);
                        } else {
                            this._outImg.pixels[var22] = 11184810;
                        }
                    } else if(var27 >= 0 && var27 < this._inImg.pixels.length) {
                        this._outImg.pixels[var22] = this._inImg.pixels[var27];
                    } else {
                        this._outImg.pixels[var22] = 11184810;
                    }
                }
            }

            this._stillRunning.countDown();
        }
    }

    int mylerp(int var1, int var2, int var3, int var4, float var5, float var6) {
        int var7 = var1 >> 16 & 255;
        int var8 = var1 >> 8 & 255;
        int var9 = var1 & 255;
        int var10 = var2 >> 16 & 255;
        int var11 = var2 >> 8 & 255;
        int var12 = var2 & 255;
        int var13 = var3 >> 16 & 255;
        int var14 = var3 >> 8 & 255;
        int var15 = var3 & 255;
        int var16 = var4 >> 16 & 255;
        int var17 = var4 >> 8 & 255;
        int var18 = var4 & 255;
        int var19 = (int)(((float)var7 * (1.0F - var5) + (float)var10 * var5) * (1.0F - var6) + ((float)var13 * (1.0F - var5) + (float)var16 * var5) * var6);
        int var20 = (int)(((float)var8 * (1.0F - var5) + (float)var11 * var5) * (1.0F - var6) + ((float)var14 * (1.0F - var5) + (float)var17 * var5) * var6);
        int var21 = (int)(((float)var9 * (1.0F - var5) + (float)var12 * var5) * (1.0F - var6) + ((float)var15 * (1.0F - var5) + (float)var18 * var5) * var6);
        return var19 << 16 | var20 << 8 | var21;
    }

    public static void interpol() {
        if(interpolate) {
            interpolate = false;
        } else {
            interpolate = true;
        }

    }
}
