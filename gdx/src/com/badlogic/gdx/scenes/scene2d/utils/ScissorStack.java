/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/** A stack of {@link Rectangle} objects to be used for clipping via {@link GL20#glScissor(int, int, int, int)}. When a new
 * Rectangle is pushed onto the stack, it will be merged with the current top of stack. The minimum area of overlap is then set as
 * the real top of the stack.
 * @author mzechner */
public class ScissorStack {
	private static Array<Rectangle> scissors = new Array<Rectangle>();
	static Vector3 tmp = new Vector3();
	static final Rectangle viewport = new Rectangle();

	/** Pushes a new scissor {@link Rectangle} onto the stack, merging it with the current top of the stack. The minimal area of
	 * overlap between the top of stack rectangle and the provided rectangle is pushed onto the stack. This will invoke
	 * {@link GL20#glScissor(int, int, int, int)} with the final top of stack rectangle. In case no scissor is yet on the stack
	 * this will also enable {@link GL20#GL_SCISSOR_TEST} automatically.
	 * <p>
	 * Any drawing should be flushed before pushing scissors.
	 * @return true if the scissors were pushed. false if the scissor area was zero, in this case the scissors were not pushed and
	 *         no drawing should occur. */
	public static boolean pushScissors (Rectangle scissor) {
		fix(scissor);

		if (scissors.size == 0) {
			if (scissor.getWidth() < 1 || scissor.getHeight() < 1) return false;
			Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		} else {
			// merge scissors
			Rectangle parent = scissors.get(scissors.size - 1);
			float minX = Math.max(parent.getX(), scissor.getY());
			float maxX = Math.min(parent.getX() + parent.getWidth(), scissor.getX() + scissor.getWidth());
			if (maxX - minX < 1) return false;

			float minY = Math.max(parent.getY(), scissor.getY());
			float maxY = Math.min(parent.getY() + parent.getHeight(), scissor.getY() + scissor.getHeight());
			if (maxY - minY < 1) return false;

			scissor.setX(minX);
			scissor.setY(minY);
			scissor.setWidth(maxX - minX);
			scissor.setHeight(Math.max(1, maxY - minY));
		}
		scissors.add(scissor);
		HdpiUtils.glScissor((int)scissor.getX(), (int)scissor.getY(), (int)scissor.getWidth(), (int)scissor.getHeight());
		return true;
	}

	/** Pops the current scissor rectangle from the stack and sets the new scissor area to the new top of stack rectangle. In case
	 * no more rectangles are on the stack, {@link GL20#GL_SCISSOR_TEST} is disabled.
	 * <p>
	 * Any drawing should be flushed before popping scissors. */
	public static Rectangle popScissors () {
		Rectangle old = scissors.pop();
		if (scissors.size == 0)
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		else {
			Rectangle scissor = scissors.peek();
			HdpiUtils.glScissor((int)scissor.getX(), (int)scissor.getY(), (int)scissor.getWidth(), (int)scissor.getHeight());
		}
		return old;
	}

	public static Rectangle peekScissors () {
		return scissors.peek();
	}

	private static void fix (Rectangle rect) {
		rect.setX(Math.round(rect.getX()));
		rect.setY(Math.round(rect.getY()));
		rect.setWidth(Math.round(rect.getWidth()));
		rect.setHeight(Math.round(rect.getHeight()));
		if (rect.getWidth() < 0) {
			rect.setWidth(-rect.getWidth());
			rect.setX(rect.getX() - rect.getWidth());
		}
		if (rect.getHeight() < 0) {
			rect.setHeight(-rect.getHeight());
			rect.setY(rect.getY() - rect.getHeight());
		}
	}

	/** Calculates a scissor rectangle using 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight() as the viewport.
	 * @see #calculateScissors(Camera, float, float, float, float, Matrix4, Rectangle, Rectangle) */
	public static void calculateScissors (Camera camera, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
		calculateScissors(camera, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), batchTransform, area, scissor);
	}

	/** Calculates a scissor rectangle in OpenGL ES window coordinates from a {@link Camera}, a transformation {@link Matrix4} and
	 * an axis aligned {@link Rectangle}. The rectangle will get transformed by the camera and transform matrices and is then
	 * projected to screen coordinates. Note that only axis aligned rectangles will work with this method. If either the Camera or
	 * the Matrix4 have rotational components, the output of this method will not be suitable for
	 * {@link GL20#glScissor(int, int, int, int)}.
	 * @param camera the {@link Camera}
	 * @param batchTransform the transformation {@link Matrix4}
	 * @param area the {@link Rectangle} to transform to window coordinates
	 * @param scissor the Rectangle to store the result in */
	public static void calculateScissors (Camera camera, float viewportX, float viewportY, float viewportWidth,
		float viewportHeight, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
		tmp.set(area.getX(), area.getY(), 0);
		tmp.mul(batchTransform);
		camera.project(tmp, viewportX, viewportY, viewportWidth, viewportHeight);
		scissor.setX(tmp.x);
		scissor.setY(tmp.y);

		tmp.set(area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0);
		tmp.mul(batchTransform);
		camera.project(tmp, viewportX, viewportY, viewportWidth, viewportHeight);
		scissor.setWidth(tmp.x - scissor.getX());
		scissor.setHeight(tmp.y - scissor.getY());
	}

	/** @return the current viewport in OpenGL ES window coordinates based on the currently applied scissor */
	public static Rectangle getViewport () {
		if (scissors.size == 0) {
			viewport.set(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			return viewport;
		} else {
			Rectangle scissor = scissors.peek();
			viewport.set(scissor);
			return viewport;
		}
	}
}
