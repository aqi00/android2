package com.example.event.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexParameterf;

public class PanoramaUtil {
    private static String SOURCE_DEFAULT_NAME_FRAGMENT = "fragment.glsl";
    private static String SOURCE_DEFAULT_NAME_VERTEX = "vertex.glsl";

    public static int getProgram(Context context) {
        String vertexStr = getShaderSource(context, SOURCE_DEFAULT_NAME_VERTEX);
        String fragmentStr = getShaderSource(context, SOURCE_DEFAULT_NAME_FRAGMENT);
        int program = glCreateProgram();
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexStr);
        glShaderSource(fragmentShader, fragmentStr);
        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        return program;
    }

    private static String getShaderSource(Context context, String sourseName) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    context.getAssets().open(sourseName)));
            String tempStr;
            while (null != (tempStr = br.readLine())) {
                shaderSource.append(tempStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shaderSource.toString();
    }

    static int initTexture(Context context, int drawableId) {
        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        int textureId = textures[0];
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        return textureId;
    }

    public static ArrayList<Float> getPanoramaVertexList(int perVertex, double perRadius) {
        ArrayList<Float> vetexList = new ArrayList<Float>();
        for (int i = 0; i < perVertex; i++) {
            for (int j = 0; j < perVertex; j++) {
                float x1 = (float) (Math.sin(i * perRadius / 2) * Math.cos(j
                        * perRadius));
                float z1 = (float) (Math.sin(i * perRadius / 2) * Math.sin(j
                        * perRadius));
                float y1 = (float) Math.cos(i * perRadius / 2);

                float x2 = (float) (Math.sin((i + 1) * perRadius / 2) * Math
                        .cos(j * perRadius));
                float z2 = (float) (Math.sin((i + 1) * perRadius / 2) * Math
                        .sin(j * perRadius));
                float y2 = (float) Math.cos((i + 1) * perRadius / 2);

                float x3 = (float) (Math.sin((i + 1) * perRadius / 2) * Math
                        .cos((j + 1) * perRadius));
                float z3 = (float) (Math.sin((i + 1) * perRadius / 2) * Math
                        .sin((j + 1) * perRadius));
                float y3 = (float) Math.cos((i + 1) * perRadius / 2);

                float x4 = (float) (Math.sin(i * perRadius / 2) * Math
                        .cos((j + 1) * perRadius));
                float z4 = (float) (Math.sin(i * perRadius / 2) * Math
                        .sin((j + 1) * perRadius));
                float y4 = (float) Math.cos(i * perRadius / 2);

                vetexList.add(x1);
                vetexList.add(y1);
                vetexList.add(z1);

                vetexList.add(x2);
                vetexList.add(y2);
                vetexList.add(z2);

                vetexList.add(x3);
                vetexList.add(y3);
                vetexList.add(z3);

                vetexList.add(x3);
                vetexList.add(y3);
                vetexList.add(z3);

                vetexList.add(x4);
                vetexList.add(y4);
                vetexList.add(z4);

                vetexList.add(x1);
                vetexList.add(y1);
                vetexList.add(z1);
            }
        }
        return vetexList;
    }

    public static ArrayList<Float> getPanoramaTextureList(int perVertex) {
        ArrayList<Float> textureList = new ArrayList<Float>();
        double perW = 1 / (float) perVertex;
        double perH = 1 / (float) (perVertex);
        for (int i = 0; i < perVertex; i++) {
            for (int j = 0; j < perVertex; j++) {
                float w1 = (float) (i * perH);
                float h1 = (float) (j * perW);

                float w2 = (float) ((i + 1) * perH);
                float h2 = (float) (j * perW);

                float w3 = (float) ((i + 1) * perH);
                float h3 = (float) ((j + 1) * perW);

                float w4 = (float) (i * perH);
                float h4 = (float) ((j + 1) * perW);

                textureList.add(h1);
                textureList.add(w1);

                textureList.add(h2);
                textureList.add(w2);

                textureList.add(h3);
                textureList.add(w3);

                textureList.add(h3);
                textureList.add(w3);

                textureList.add(h4);
                textureList.add(w4);

                textureList.add(h1);
                textureList.add(w1);
            }
        }
        return textureList;
    }

}
