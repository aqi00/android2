package com.example.event.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.translateM;

public class PanoramaRender implements Renderer {
    private final static String TAG = "PanoramaRender";
    private Context mContext;
    private int mAPositionHandler;
    private int mUProjectMatrixHandler;
    private int mATextureCoordHandler;
    private float[] mProjectMatrix = new float[16];
    private int mVertexCount;
    private FloatBuffer mVertexBuff;
    private FloatBuffer mTextureBuff;
    private int mDrawableId;
    private final float mCurrMatrix[] = new float[16];
    private final float mMVPMatrix[] = new float[16];
    public float xAngle = 0f;
    public float yAngle = 90f;
    public float zAngle;

    public PanoramaRender(Context context) {
        mContext = context;
        initData();
    }

    // 初始化顶点数据和纹理坐标
    private void initData() {
        int perVertex = 36;
        double perRadius = 2 * Math.PI / (float) perVertex;
        ArrayList<Float> vetexList = PanoramaUtil.getPanoramaVertexList(perVertex, perRadius);
        ArrayList<Float> textureList = PanoramaUtil.getPanoramaTextureList(perVertex);
        mVertexCount = vetexList.size() / 3;
        float texture[] = new float[mVertexCount * 2];
        for (int i = 0; i < texture.length; i++) {
            texture[i] = textureList.get(i);
        }
        mTextureBuff = ByteBuffer.allocateDirect(texture.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuff.put(texture);
        mTextureBuff.position(0);

        float vetex[] = new float[vetexList.size()];
        for (int i = 0; i < vetex.length; i++) {
            vetex[i] = vetexList.get(i);
        }
        mVertexBuff = ByteBuffer.allocateDirect(vetex.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuff.put(vetex);
        mVertexBuff.position(0);
    }

    // 设置全景图片的资源编号
    public void setDrawableId(int drawableId) {
        mDrawableId = drawableId;
    }

    // 在表面创建时触发
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {}

    // 在表面变更时触发
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        float ratio = width / (float) height;
        frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 1, 20);
        Matrix.setIdentityM(mCurrMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        translateM(mProjectMatrix, 0, 0, 0, -2);
        scaleM(mProjectMatrix, 0, 4, 4, 4);

        int program = PanoramaUtil.getProgram(mContext);
        glUseProgram(program);
        mAPositionHandler = glGetAttribLocation(program, "aPosition");
        mUProjectMatrixHandler = glGetUniformLocation(program, "uProjectMatrix");
        mATextureCoordHandler = glGetAttribLocation(program, "aTextureCoord");
        Log.d(TAG, "mAPositionHandler:" + mAPositionHandler);
        Log.d(TAG, "mUProjectMatrixHandler:" + mUProjectMatrixHandler);
        Log.d(TAG, "mATextureCoordHandler:" + mATextureCoordHandler);

        glVertexAttribPointer(mAPositionHandler, 3, GL_FLOAT, false, 0, mVertexBuff);
        glVertexAttribPointer(mATextureCoordHandler, 2, GL_FLOAT, false, 0, mTextureBuff);
        glEnableVertexAttribArray(mAPositionHandler);
        glEnableVertexAttribArray(mATextureCoordHandler);
    }

    // 执行框架绘制动作
    public void onDrawFrame(GL10 arg0) {
        rotateM(mCurrMatrix, 0, -xAngle, 1, 0, 0);
        rotateM(mCurrMatrix, 0, -yAngle, 0, 1, 0);
        rotateM(mCurrMatrix, 0, -zAngle, 0, 0, 1);
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCurrMatrix, 0);
        Matrix.setIdentityM(mCurrMatrix, 0);
        int textrueID = PanoramaUtil.initTexture(mContext, mDrawableId);
        Log.d(TAG, "textureID:" + textrueID);
        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textrueID);
        glUniformMatrix4fv(mUProjectMatrixHandler, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, mVertexCount);
    }

}
