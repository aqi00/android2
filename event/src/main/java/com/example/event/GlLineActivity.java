package com.example.event;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.event.opengl.VertexUtil;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GlLineActivity extends AppCompatActivity {
    private GLSurfaceView glsv_content; // 声明一个图形库表面视图对象
    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private int mPointCount; // 组成立方体的点个数
    private int mType; // 形状的类型
    private int mDivide = 20; // 将经纬度等分的面数
    private float mRadius = 4; // 球半径
    private int mAngle = 0; // 旋转角度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_line);
        initShapeSpinner();
        mPointCount = VertexUtil.getCubePointCount();
        // 从布局文件中获取名叫glsv_content的图形库表面视图
        glsv_content = findViewById(R.id.glsv_content);
        // 给OpenGL的表面视图注册三维图形的渲染器
        glsv_content.setRenderer(new GLRender());
    }

    // 初始化形状下拉框
    private void initShapeSpinner() {
        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, shapeArray);
        Spinner sp_shape = findViewById(R.id.sp_shape);
        sp_shape.setPrompt("请选择三维物体形状");
        sp_shape.setAdapter(shapeAdapter);
        sp_shape.setOnItemSelectedListener(new ShapeSelectedListener());
        sp_shape.setSelection(0);
    }

    private String[] shapeArray = { "静止立方体", "静止球体", "旋转立方体", "旋转球体" };
    class ShapeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mType = arg2;
            // 暂停绘制三维图形
            glsv_content.onPause();
            mVertices.clear();
            if (mType == 0 || mType == 2) {
                mVertices = VertexUtil.getCubeVertexs();
            } else if (mType == 1 || mType == 3) {
                mVertices = VertexUtil.getBallVertices(mDivide, mRadius);
            }
            // 恢复绘制三维图形
            glsv_content.onResume();
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 定义一个三维图形的渲染器
    private class GLRender implements GLSurfaceView.Renderer {
        // 在表面创建时触发
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 设置白色背景。0.0f相当于00，1.0f相当于FF
            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            // 启用阴影平滑
            gl.glShadeModel(GL10.GL_SMOOTH);
        }

        // 在表面变更时触发
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // 设置输出屏幕大小
            gl.glViewport(0, 0, width, height);
            // 设置投影矩阵，对应gluPerspective（调整相机）、glFrustumf（调整透视投影）、glOrthof（调整正投影）
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // 重置投影矩阵，即去掉所有的平移、缩放、旋转操作
            gl.glLoadIdentity();
            // 设置透视图视窗大小
            GLU.gluPerspective(gl, 40, (float) width / height, 0.1f, 20.0f);
            // 选择模型观察矩阵，对应gluLookAt（人动）、glTranslatef/glScalef/glRotatef（物动）
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            // 重置模型矩阵
            gl.glLoadIdentity();
        }

        // 执行框架绘制动作
        public void onDrawFrame(GL10 gl) {
            // 清除屏幕和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            // 重置当前的模型观察矩阵
            gl.glLoadIdentity();
            // 设置画笔颜色
            gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
            // 设置观测点。eyeXYZ表示眼睛坐标，centerXYZ表示原点坐标，upX=1表示X轴朝上，upY=1表示Y轴朝上，upZ=1表示Z轴朝上
            GLU.gluLookAt(gl, 10.0f, 8.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            // 旋转图形
            if (mType == 2 || mType == 3) {
                gl.glRotatef(mAngle, 0, 0, -1);
                gl.glRotatef(mAngle, 0, -1, 0);
                mAngle++;
            } else {
                mAngle = 0;
            }
            // 沿x轴方向移动1个单位
            // gl.glTranslatef(1, 0, 0);
            // x，y，z方向缩放0.1倍
            // gl.glScalef(0.1f, 0.1f, 0.1f);
            if (mType == 0 || mType == 2) {
                drawCube(gl);
            } else if (mType == 1 || mType == 3) {
                drawBall(gl);
            }
        }
    }

    // 绘制立方体
    private void drawCube(GL10 gl) {
        // 启用顶点开关
        //GL_VERTEX_ARRAY顶点数组
        //GL_COLOR_ARRAY颜色数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (FloatBuffer buffer : mVertices) {
            // 将顶点坐标传给 OpenGL 管道
            //size: 每个顶点有几个数值描述。必须是2，3 ，4 之一。
            //type: 数组中每个顶点的坐标类型。取值：GL_BYTE,GL_SHORT, GL_FIXED, GL_FLOAT。
            //stride：数组中每个顶点间的间隔，步长（字节位移）。取值若为0，表示数组是连续的
            //pointer：即存储顶点的Buffer
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
            // 用画线的方式将点连接并画出来
            //GL_POINTS ————绘制独立的点
            //GL_LINE_STRIP————绘制连续的线段，不封闭
            //GL_LINE_LOOP————绘制连续的线段，封闭
            //GL_LINES————顶点两两连接，为多条线段构成
            //GL_TRIANGLES————每隔三个顶点构成一个三角形
            //GL_TRIANGLE_STRIP————每相邻三个顶点组成一个三角形
            //GL_TRIANGLE_FAN————以一个点为三角形公共顶点，组成一系列相邻的三角形
            //first：一般填0
            //count：每个面画的线段-1。如果count=3表示这个面画两条线段
            gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, mPointCount);
        }
        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    // 绘制球体
    private void drawBall(GL10 gl) {
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 每次画两条相邻的纬度线
        for (int i = 0; i <= mDivide && i < mVertices.size(); i++) {
            // 将顶点坐标传给 OpenGL 管道
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices.get(i));
            // 用画线的方式将点连接并画出来
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mDivide * 2 + 2);
        }
        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

}
