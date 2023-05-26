//package com.example.flashcardsapp;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.EmbossMaskFilter;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Shader;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//
//public class Frfrfr extends View {
//
//    Paint mBitmapPaint;
//    Bitmap tileImage;
//    BitmapShader shader;
//    Paint tile;
//    Paint eraserPaint;
//    Path mPath;
//    EmbossMaskFilter mEmboss;
//    float myWidth;
//    float myHeight;
//    Canvas mCanvas;
//    Bitmap mBitmap;
//
//
//    public void KNDrawingSurfaceView(Context c, float width, float height, KNSketchBookActivity parent) {
//        super(c);
////
//        myWidth = width;
//        myHeight = height;
//
//        mBitmap = Bitmap.createBitmap((int) myWidth, (int) myHeight, Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(mBitmap);
//
//        _parent = parent;
//
//
//        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
//
//
//        tile = new Paint();
//
//        tileImage = BitmapFactory.decodeResource(getResources(), R.drawable.checkerpattern);
//        shader = new BitmapShader(tileImage, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        tile.setShader(shader);
//
//
//        mPath = new Path();
//        eraserPaint = new Paint();
//        eraserPaint.setAlpha(0x00);
//        eraserPaint.setColor(Color.TRANSPARENT);
//        eraserPaint.setStrokeWidth(60);
//        eraserPaint.setStyle(Paint.Style.STROKE);
//        //eraserPaint.setMaskFilter(null);
//        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        eraserPaint.setAntiAlias(true);
//
//        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//
//
//
//        mCanvas.drawRect(0, 0, myWidth, myHeight, tile);
//
//        mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//    }
//
//    public Frfrfr(Context context) {
//        super(context);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//
//        super.onSizeChanged(w, h, oldw, oldh);
//
//
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        if (!_parent.isDrawerOpen()&&mPaint!=null) {
//            Log.v("onDraw:", "curent paths size:" + paths.size());
//
//            //mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            //canvas.drawPath(mPath, mPaint);
//            for (int i=0;i< paths.size();i++) {
//                tempPaint =  paints.get(i);
//                eraserPaint.setStrokeWidth(tempPaint.getStrokeWidth());
//                if(fills.get(i)){
//                    tempPaint.setStyle(Style.FILL_AND_STROKE);
//                    eraserPaint.setStyle(Style.FILL_AND_STROKE);
//                }else{
//                    tempPaint.setStyle(Style.STROKE);
//                    eraserPaint.setStyle(Style.STROKE);
//                }
//                if(erasers.get(i)){
//                    //tempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                    canvas.drawPath(paths.get(i), eraserPaint);
//                }else{
//                    //tempPaint.setXfermode(null);
//                    canvas.drawPath(paths.get(i), tempPaint);
//                }
//                //canvas.drawPath(paths.get(i), tempPaint);
//            }
//            if(_parent.toggleFill.isChecked()){
//                mPaint.setStyle(Style.FILL_AND_STROKE);
//                eraserPaint.setStyle(Style.FILL_AND_STROKE);
//
//            }else{
//                mPaint.setStyle(Style.STROKE);
//                eraserPaint.setStyle(Style.STROKE);
//            }
//            if(_parent.toggleErase.isChecked()){
//                //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                canvas.drawPath(mPath, eraserPaint);
//            }else{
//                //mPaint.setXfermode(null);
//                canvas.drawPath(mPath, mPaint);
//            }
//            //canvas.drawPath(mPath, mPaint);
//        }
//    }
//
//    public void onClickUndo() {
//
//        if (paths.size() > 0) {
//            undonePaths.add(paths.remove(paths.size() - 1));
//            undonePaints.add(paints.remove(paints.size() - 1));
//            undoneFills.add(fills.remove(fills.size() - 1));
//            undoneErasers.add(erasers.remove(erasers.size() - 1));
//            clearCanvasCache();
//            invalidate();
//        } else {
//
//        }
//        _parent.checkButtonStates();
//    }
//
//    public void onClickRedo() {
//
//        if (undonePaths.size() > 0) {
//            paths.add(undonePaths.remove(undonePaths.size() - 1));
//            paints.add(undonePaints.remove(undonePaints.size() - 1));
//            fills.add(undoneFills.remove(undoneFills.size() - 1));
//            erasers.add(undoneErasers.remove(undoneErasers.size() - 1));
//            clearCanvasCache();
//            invalidate();
//        } else {
//
//        }
//        _parent.checkButtonStates();
//    }
//
//    public void onClickClear() {
//
//        paths.clear();
//        paints.clear();
//        fills.clear();
//        erasers.clear();
//        undoneFills.clear();
//        undonePaths.clear();
//        undonePaints.clear();
//        undoneErasers.clear();
//        clearCanvasCache();
//        invalidate();
//        _parent.checkButtonStates();
//    }
//
//    public void saveDrawing() {
//
//        FileOutputStream outStream = null;
//        String fileName = "tempTag";
//        try {
//
//            outStream = new FileOutputStream("/sdcard/" + fileName + ".png");
//
//            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            outStream.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//        }
//
//    }
//
//    private float mX, mY;
//
//    private static final float TOUCH_TOLERANCE = 4;
//
//    private void touch_start(float x, float y) {
//
//        undonePaths.clear();
//        undonePaints.clear();
//        undoneFills.clear();
//        mPath.reset();
//        mPath.moveTo(x, y);
//
//        mX = x;
//        mY = y;
//    }
//
//    private void touch_move(float x, float y) {
//
//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(y - mY);
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//            mX = x;
//            mY = y;
//        }
//    }
//
//    private void touch_up() {
//
//        mPath.lineTo(mX, mY);
//        // commit the path to our offscreen
//        if(_parent.toggleErase.isChecked()){
//            mCanvas.drawPath(mPath, eraserPaint);
//            erasers.add(true);
//            paints.add(eraserPaint);
//        }else{
//            mCanvas.drawPath(mPath, mPaint);
//            erasers.add(false);
//            paints.add(mPaint);
//        }
//
//        // kill this so we don't double draw
//
//        paths.add(mPath);
//
//
//        if(_parent.toggleFill.isChecked()){
//            fills.add(true);
//        }else{
//            fills.add(false);
//        }
//        if(_parent.toggleErase.isChecked()){
//            erasers.add(true);
//        }else{
//            erasers.add(false);
//        }
//
//
//        _parent.checkButtonStates();
//        mPath = new Path();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(mPaint==null &&!_parent._showingAlert){
//            _parent.showNoPaintAlert();
//        }
//
//        if (!_parent.isDrawerOpen()&&mPaint!=null) {
//            float x = event.getX();
//            float y = event.getY();
//            if (x > myWidth) {
//                x = myWidth;
//
//            }
//            if (y > myHeight) {
//                y = myHeight;
//
//            }
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    touch_start(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    touch_move(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    touch_up();
//                    invalidate();
//                    break;
//            }
//            return true;
//        } else {
//            return true;
//        }
//    }
//
//    public void clearCanvasCache() {
//
//        mBitmap = Bitmap.createBitmap((int) myWidth, (int) myHeight, Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(mBitmap);
//    }
//
//}
