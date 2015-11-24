package com.gurella.engine.graphics.vector;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.GlUniforms.ImageType;

public class GlContext implements Poolable {
	private boolean antiAlias;
	private boolean stencilStrokes;
	private boolean debug;
	
	private int vertexBuffer;
	private final CanvasShader shader = new CanvasShader();
	private FloatBuffer view = BufferUtils.newFloatBuffer(2);
	
	int activeTexture;
	int boundTexture;
	int boundGradientImage;
	int stencilMask;
	int stencilFunction;
	int stencilFunctionRef;
	int stencilFuncMask;
	BlendMode blendMode = BlendMode.over;
	
	private final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);

	FloatBuffer vertsBuffer;
	
	void init(int width, int height, boolean antiAlias, boolean stencilStrokes, boolean debug) {
		setViewport(width, height);
		this.antiAlias = antiAlias;
		this.stencilStrokes = stencilStrokes;
		this.debug = debug;
		
		shader.init(antiAlias, debug);
		
		tmpHandle.clear();
		gl.glGenBuffers(1, tmpHandle);
		vertexBuffer = tmpHandle.get(0);
		checkError("create done");
		gl.glFinish();
	}

	private void setViewport(int width, int height) {
		view.put(0, width);
		view.put(1, height);
		view.rewind();
	}
	
	int getWidth() {
		return (int) view.get(0);
	}
	
	int getHeight() {
		return (int) view.get(1);
	}
	
	private void bindTexture(int tex) {
		if (boundTexture != tex) {
			boundTexture = tex;
			if(activeTexture != 0) {
				gl.glActiveTexture(GL20.GL_TEXTURE0);
				activeTexture = 0;
			}
			gl.glBindTexture(GL20.GL_TEXTURE_2D, tex);
		}
	}
	
	private void bindGradient(int gradientImage) {
		if (boundGradientImage != gradientImage) {
			boundGradientImage = gradientImage;
			if(activeTexture != 1) {
				gl.glActiveTexture(GL20.GL_TEXTURE1);
				activeTexture = 1;
			}
			gl.glBindTexture(GL20.GL_TEXTURE_2D, gradientImage);
		}
	}
	
	private void stencilMask(int mask) {
		if (stencilMask != mask) {
			stencilMask = mask;
			gl.glStencilMask(mask);
		}
	}
	
	private void stencilFunction(int func, int ref, int mask) {
		if ((stencilFunction != func) || (stencilFunctionRef != ref) || (stencilFuncMask != mask)) {
			stencilFunction = func;
			stencilFunctionRef = ref;
			stencilFuncMask = mask;
			gl.glStencilFunc(func, ref, mask);
		}
	}
	
	private void setBlendMode(BlendMode blendMode) {
		if (this.blendMode != blendMode) {
			
			if(this.blendMode == BlendMode.src) {
				gl.glEnable(GL20.GL_BLEND);
			} 
			
			this.blendMode = blendMode;
					
			if(blendMode == BlendMode.src) {
				gl.glDisable(GL20.GL_BLEND);
				return;
			}
			
			switch (blendMode) {
			case clear:
				gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ZERO);
				break;
			case src:
				break;
			case dst:
				gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
				break;
			case over:
				gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case overReverse:
				gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE);
				break;
			case in:
				gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ZERO);
				break;
			case inReverse:
				gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
				break;
			case out:
				gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ZERO);
				break;
			case outReverse:
				gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case atop:
				gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case atopReverse:
				gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_SRC_ALPHA);
				break;
			case xor:
				gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case add:
				gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
				break;
			}
		}
	}
	
	private Buffer getVertsBuffer(Array<GlCall> calls, int vertFloats) {
		ensureVertsBufferCapacity(vertFloats);
		
		for(GlCall call : calls){
			for(GlPathComponent path : call.components){
				path.triangleFanVerticesOffset = vertsBuffer.position() / 4;
				for(Vertex vertex : path.triangleFanVertices) {
					fillVertsBuffer(vertex);
				}
				
				path.triangleStripVerticesOffset = vertsBuffer.position() / 4;
				for(Vertex vertex : path.triangleStripVertices) {
					fillVertsBuffer(vertex);
				}
			}
			
			call.triangleVerticesOffset = vertsBuffer.position() / 4;
			for(Vertex vertex : call.triangleVertices) {
				fillVertsBuffer(vertex);
			}
		}
		
		return vertsBuffer.rewind();
	}

	private void fillVertsBuffer(Vertex vertex) {
		vertsBuffer.put(vertex.x);
		vertsBuffer.put(vertex.y);
		vertsBuffer.put(vertex.u);
		vertsBuffer.put(vertex.v);
	}

	private void ensureVertsBufferCapacity(int vertFloats) {
		if (vertsBuffer == null || vertsBuffer.capacity() < vertFloats) {
			vertsBuffer = BufferUtils.newFloatBuffer(vertFloats);
		}
		
		vertsBuffer.clear();
		vertsBuffer.limit(vertFloats);
	}
	
	private static int getVertFloatsCount(Array<GlCall> calls) {
		int vertFloats = 0;
		for(GlCall call : calls){
			for(GlPathComponent path : call.components){
				vertFloats += path.triangleFanVertices.size;
				vertFloats += path.triangleStripVertices.size;
			}
			vertFloats += call.triangleVertices.size;
		}
		
		return vertFloats * 4;
	}

	public void render(Array<GlCall> calls) {
		if (calls.size > 0) {
			// Setup required GL state.
			gl.glUseProgram(shader.progamHandle);
			
			gl.glEnable(GL20.GL_BLEND);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			gl.glEnable(GL20.GL_CULL_FACE);
			gl.glCullFace(GL20.GL_BACK);
			gl.glFrontFace(GL20.GL_CCW);
			
			gl.glDisable(GL20.GL_DEPTH_TEST);
			gl.glDisable(GL20.GL_SCISSOR_TEST);
			
			gl.glColorMask(true, true, true, true);
			gl.glStencilMask(0xffffffff);
			gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
			gl.glStencilFunc(GL20.GL_ALWAYS, 0, 0xffffffff);
			
			gl.glActiveTexture(GL20.GL_TEXTURE1);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
			
			gl.glActiveTexture(GL20.GL_TEXTURE0);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
			
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertexBuffer);
			int vertFloats = getVertFloatsCount(calls);
			Buffer filledVertsBuffer = getVertsBuffer(calls, vertFloats);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, vertFloats * 4, filledVertsBuffer, GL20.GL_STREAM_DRAW);
			
			gl.glEnableVertexAttribArray(0);
			gl.glEnableVertexAttribArray(1);
			gl.glVertexAttribPointer(0, 2, GL20.GL_FLOAT, false, 4 * 4, 0);
			gl.glVertexAttribPointer(1, 2, GL20.GL_FLOAT, false, 4 * 4, 2 * 4);

			// Set view and texture just once per frame.
			gl.glUniform1i(shader.imageUniformLocation, 0);
			gl.glUniform1i(shader.gradientImageUniformLocation, 1);
			gl.glUniform2fv(shader.viewSizeUniformLocation, 1, view);

			resetStateData();
			renderCalls(calls);

			gl.glDisableVertexAttribArray(0);
			gl.glDisableVertexAttribArray(1);
			gl.glDisable(GL20.GL_CULL_FACE);
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
			gl.glUseProgram(0);
			bindTexture(0);
			bindGradient(0);
		}
	}

	private void resetStateData() {
		//TODO convert to state object
		activeTexture = 0;
		boundTexture = 0;
		boundGradientImage = 0;
		stencilMask = 0xffffffff;
		stencilFunction = GL20.GL_ALWAYS;
		stencilFunctionRef = 0;
		stencilFuncMask = 0xffffffff;
		blendMode = BlendMode.over;
	}

	private void renderCalls(Array<GlCall> calls) {
		for (int i = 0; i < calls.size; i++) {
			GlCall call = calls.get(i);
			setBlendMode(call.blendMode);
			
			switch (call.callType) {
			case convexFill:
				convexFill(call);
				break;
			case fill:
				/*if(isClipped(call)) {
					fillClipped(call);
				} else {
					fill(call);
				}*/
				fill(call);
				break;
			case stroke:
				stroke(call);
				break;
			case triangles:
				triangles(call);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public void setUniforms(GlUniforms uniforms) {
		gl.glUniform4fv(shader.fragUniformLocation, GlUniforms.UNIFORMARRAY_SIZE, uniforms.getUniformArray());

		if (uniforms.imageType != ImageType.none) {
			bindTexture(uniforms.imageHandle);
			checkError("tex paint tex");
		} else {
			bindTexture(0);
		}
		
		if (uniforms.gradientType != GradientType.none) {
			bindGradient(uniforms.gradient.getTexturehandle());
			checkError("tex paint tex");
		} else {
			bindGradient(0);
		}
	}
	
	private void convexFill(GlCall call) {
		setUniforms(call.getUniforms(0));
		checkError("convex fill");
		
		Array<GlPathComponent> components = call.components;

		for (int i = 0; i < components.size; i++) {
			GlPathComponent component = components.get(i);
			gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, component.triangleFanVerticesOffset, component.triangleFanVertices.size);
		}

		if (antiAlias) {
			// Draw fringes
			for (int i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}
		}
	}
	
	private void fill(GlCall call) {
		// Draw shapes
		gl.glEnable(GL20.GL_STENCIL_TEST);
		stencilMask(0xff);
		stencilFunction(GL20.GL_ALWAYS, 0, 0xff);
		gl.glColorMask(false, false, false, false);

		// set bindpoint for solid loc
		setUniforms(GlUniforms.stencilInstance);
		checkError("fill stencil");

		gl.glStencilOpSeparate(GL20.GL_FRONT, GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_INCR_WRAP);
		gl.glStencilOpSeparate(GL20.GL_BACK, GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_DECR_WRAP);
		gl.glDisable(GL20.GL_CULL_FACE);
		
		Array<GlPathComponent> components = call.components;
		
		for (int i = 0; i < components.size; i++) {
			GlPathComponent component = components.get(i);
			gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, component.triangleFanVerticesOffset, component.triangleFanVertices.size);
		}
		
		gl.glEnable(GL20.GL_CULL_FACE);
		
		// Draw anti-aliased pixels
		gl.glColorMask(true, true, true, true);
		setUniforms(call.getUniforms(0));
		checkError("fill fill");

		if (antiAlias) {
			stencilFunction(GL20.GL_EQUAL, 0x00, 0xff);
			gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);

			// Draw fringes
			for (int i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}
		}
		
		// Draw fill
		stencilFunction(GL20.GL_NOTEQUAL, 0x0, 0xff);
		gl.glStencilOp(GL20.GL_ZERO, GL20.GL_ZERO, GL20.GL_ZERO);
		gl.glDrawArrays(GL20.GL_TRIANGLES, call.triangleVerticesOffset, call.triangleVertices.size);

		gl.glDisable(GL20.GL_STENCIL_TEST);
	}
	
	private void stroke(GlCall call) {
		int i;
		Array<GlPathComponent> components = call.components;

		if (stencilStrokes) {
			gl.glEnable(GL20.GL_STENCIL_TEST);
			stencilMask(0xff);

			// Fill the stroke base without overlap
			stencilFunction(GL20.GL_EQUAL, 0x0, 0xff);
			gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_INCR);
			setUniforms(call.getUniforms(1));
			checkError("stroke fill 0");
			
			for (i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}

			// Draw anti-aliased pixels.
			setUniforms(call.getUniforms(0));
			stencilFunction(GL20.GL_EQUAL, 0x00, 0xff);
			gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
			
			for (i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}

			// Clear stencil buffer.
			gl.glColorMask(false, false, false, false);
			stencilFunction(GL20.GL_ALWAYS, 0x0, 0xff);
			gl.glStencilOp(GL20.GL_ZERO, GL20.GL_ZERO, GL20.GL_ZERO);
			checkError("stroke fill 1");
			
			for (i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}
			//TODO gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);
			
			gl.glColorMask(true, true, true, true);
			gl.glDisable(GL20.GL_STENCIL_TEST);

		} else {
			setUniforms(call.getUniforms(0));
			checkError("stroke fill");
			// Draw Strokes
			for (i = 0; i < components.size; i++) {
				GlPathComponent component = components.get(i);
				gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, component.triangleStripVerticesOffset, component.triangleStripVertices.size);
			}
		}
	}
	
	private void triangles(GlCall call) {
		setUniforms(call.getUniforms(0));
		checkError("triangles fill");
		gl.glDrawArrays(call.vertexMode.glMode, call.triangleVerticesOffset, call.triangleVertices.size);
	}

	private void checkError(String str) {
		if (debug) {
			int err = gl.glGetError();
			if (err != GL20.GL_NO_ERROR) {
				Gdx.app.error(getClass().getName(), "Error " + err + " after " + str);
			}
		}
	}
	
	@Override
	public void reset() {
		antiAlias = false;
		stencilStrokes = false;
		debug = false;
		
		activeTexture = 0;
		boundTexture = 0;
		boundGradientImage = 0;
		stencilMask = 0;
		stencilFunction = 0;
		stencilFunctionRef = 0;
		stencilFuncMask = 0;
		blendMode = BlendMode.over;
		
		view.clear();
		shader.reset();

		if (vertexBuffer != 0) {
			tmpHandle.clear();
			tmpHandle.put(0, vertexBuffer);
			tmpHandle.rewind();
			gl.glDeleteBuffers(1, tmpHandle);
			vertexBuffer = 0;
		}
	}
}
