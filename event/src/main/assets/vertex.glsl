attribute vec4 aPosition;
attribute vec2 aTextureCoord;
uniform mat4 uProjectMatrix;
varying vec2 vTextureCoord;

void main(){
	gl_Position = uProjectMatrix * aPosition;
	vTextureCoord = aTextureCoord;
}