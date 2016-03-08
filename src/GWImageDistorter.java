//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.gwoptics.graphics.Renderable;
import processing.core.PApplet;
import processing.core.PImage;

public class GWImageDistorter extends Renderable {
    private final int _numOfThreads;
    private PImage _inImg;
    private PImage _outImg;
    private PImage _tmpImg;
    private int[] _workerRanges;
    private ArrayList<ImageWorker> _workers;
    private ThreadPoolExecutor pool;
    private ArrayBlockingQueue<Runnable> _workQueue;
    private static boolean paused = false;

    public GWImageDistorter(PApplet var1, int var2, PImage var3, PImage var4) {
        super(var1);
        if(var2 < 1) {
            throw new RuntimeException("Need to specify 1 or more threads to process the image.");
        } else {
            this._workers = new ArrayList();

            int var5;
            for(var5 = 0; var5 < var2; ++var5) {
                this._workers.add(new ImageWorker());
            }

            this._workQueue = new ArrayBlockingQueue(var2);
            this.pool = new ThreadPoolExecutor(var2, 100, 1000L, TimeUnit.MILLISECONDS, this._workQueue);
            if(var4 == null) {
                throw new RuntimeException("Output image is null");
            } else {
                var5 = (int)Math.floor((double)((float)var4.pixels.length / (float)var2));
                int var6 = (int)((float)var4.pixels.length % (float)var2);
                this._workerRanges = new int[var2];

                for(int var7 = 0; var7 < var2; ++var7) {
                    if(var7 == 0) {
                        this._workerRanges[var7] = var5 + var6;
                        ((ImageWorker)this._workers.get(var7)).setOutputImage(var4);
                        ((ImageWorker)this._workers.get(var7)).setStartIndex(0);
                        ((ImageWorker)this._workers.get(var7)).setEndIndex(var5 + var6);
                    } else {
                        this._workerRanges[var7] = var5;
                        ((ImageWorker)this._workers.get(var7)).setOutputImage(var4);
                        ((ImageWorker)this._workers.get(var7)).setStartIndex(this._workerRanges[var7 - 1]);
                        ((ImageWorker)this._workers.get(var7)).setEndIndex(this._workerRanges[var7] + var5);
                    }
                }

                this._outImg = var4;
                this._numOfThreads = var2;
                if(var3 != null) {
                    this.setImageInput(var3);
                }

            }
        }
    }

    public void setImageInput(PImage var1) {
        if(var1 == null) {
            throw new NullPointerException("Image is null");
        } else {
            this._inImg = var1;
            this._tmpImg = new PImage(this._outImg.width, this._outImg.height);
            this._tmpImg.set(0, 0, var1);

            for(int var2 = 0; var2 < this._workers.size(); ++var2) {
                ImageWorker var3 = (ImageWorker)this._workers.get(var2);
                var3.setInputImage(this._inImg);
            }

        }
    }

    public void updateImage(float var1, float var2, float var3, float var4, float var5, float var6) {
        if(!paused) {
            CountDownLatch var7 = new CountDownLatch(this._numOfThreads);
            int var8 = 0;

            for(int var9 = 0; var9 < this._workers.size(); ++var9) {
                ImageWorker var10 = (ImageWorker)this._workers.get(var9);
                var10.setCountDownLatch(var7);
                var8 += this._workerRanges[var9];
                var10.time = var1;
                var10.frequency = var2;
                var10.h_cross_amp = var4;
                var10.h_cross_phs = var6;
                var10.h_plus_amp = var3;
                var10.h_plus_phs = var5;
                if(!paused) {
                    this.pool.execute(var10);
                }
            }

            try {
                var7.await(5L, TimeUnit.SECONDS);
            } catch (InterruptedException var11) {
                PApplet.println(var11.getMessage());
                var11.printStackTrace();
            }

            this._outImg.updatePixels();
        }

    }

    public void draw() {
    }

    public void pause() {
        paused = true;
    }

    public void play() {
        paused = false;
    }
}
