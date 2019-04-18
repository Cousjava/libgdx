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

package com.badlogic.gdx.backends.lwjgl;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class LwjglApplicationConfiguration {
	/** If true, OpenAL will not be used. This means {@link Application#getAudio()} returns null and the gdx-openal.jar and OpenAL
	 * natives are not needed. */
	public static boolean disableAudio;

	/** The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}. */
	private int maxNetThreads = Integer.MAX_VALUE;

	/** whether to attempt use OpenGL ES 3.0. **/
	private boolean useGL30 = false;
	/** The OpenGL context major version (the part in front of the decimal point) used to emulate OpenGL ES 3.0, when the version
	 * is not supported it will fall back to OpenGL ES 2.0 emulation. Defaults to 3.2 (major=3, minor=2). Only used when
	 * {@link #useGL30} is true. OpenGL is fully compatible with OpenGL ES 3.0 since version 4.3, setting the context version to a
	 * lower value might cause some features not to function properly. OSX requires 3.2 though.
	 * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">LWJGL OSX ContextAttribs note</a> */
	private int gles30ContextMajorVersion = 3;
	/** The OpenGL context major version (the part after the decimal point) used to emulate OpenGL ES 3.0, when the version is not
	 * supported it will fall back to OpenGL ES 2.0 emulation. Defaults to 3.2 (major=3, minor=2). Only used when {@link #useGL30}
	 * is true. OpenGL is fully compatible with OpenGL ES 3.0 since version 4.3, setting the context version to a lower value might
	 * cause some features not to function properly. OSX requires 3.2 though.
	 * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">LWJGL OSX ContextAttribs note</a> */
	private int gles30ContextMinorVersion = 2;

	/** number of bits per color channel **/
	private int r = 8, g = 8, b = 8, a = 8;
	/** number of bits for depth and stencil buffer **/
	private int depth = 16, stencil = 0;
	/** number of samples for MSAA **/
	private int samples = 0;
	/** width & height of application window **/
	private int width = 640, height = 480;
	/** x & y of application window, -1 for center **/
	private int x = -1, y = -1;
	/** fullscreen **/
	private boolean fullscreen = false;
	/** used to emulate screen densities **/
	private int overrideDensity = -1;
	/** whether to enable vsync, can be changed at runtime via {@link Graphics#setVSync(boolean)} **/
	private boolean vSyncEnabled = true;
	/** title of application **/
	private String title;
	/** whether to call System.exit() on tear-down. Needed for Webstarts on some versions of Mac OS X it seems **/
	private boolean forceExit = true;
	/** whether the window is resizable **/
	private boolean resizable = true;
	/** the maximum number of sources that can be played simultaneously */
	private int audioDeviceSimultaneousSources = 16;
	/** the audio device buffer size in samples **/
	private int audioDeviceBufferSize = 512;
	/** the audio device buffer count **/
	private int audioDeviceBufferCount = 9;
	private Color initialBackgroundColor = Color.BLACK;
	/** Target framerate when the window is in the foreground. The CPU sleeps as needed. Use 0 to never sleep. **/
	private int foregroundFPS = 60;
	/** Target framerate when the window is not in the foreground. The CPU sleeps as needed. Use 0 to never sleep, -1 to not
	 * render. **/
	private int backgroundFPS = 60;
	/** {@link LifecycleListener#pause()} and don't render when the window is minimized. **/
	private boolean pauseWhenMinimized = true;
	/** {@link LifecycleListener#pause()} (but continue rendering) when the window is not minimized and not the foreground window.
	 * To stop rendering when not the foreground window, use backgroundFPS -1. **/
	private boolean pauseWhenBackground = false;
	/** Allows software OpenGL rendering if hardware acceleration was not available.
	 * @see LwjglGraphics#isSoftwareMode() */
	private boolean allowSoftwareMode = false;
	/** Preferences directory on the desktop. Default is ".prefs/". */
	private String preferencesDirectory = ".prefs/";
	/** Preferences file type on the desktop. Default is FileType.External */
	private Files.FileType preferencesFileType = FileType.External;
	/** Callback used when trying to create a display, can handle failures, default value is null (disabled) */
	private LwjglGraphics.SetDisplayModeCallback setDisplayModeCallback;
	/** enable HDPI mode on Mac OS X **/
	private boolean useHDPI = false;

	Array<String> iconPaths = new Array();
	Array<FileType> iconFileTypes = new Array();

	/** Adds a window icon. Icons are tried in the order added, the first one that works is used. Typically three icons should be
	 * provided: 128x128 (for Mac), 32x32 (for Windows and Linux), and 16x16 (for Windows). */
	public void addIcon (String path, FileType fileType) {
		iconPaths.add(path);
		iconFileTypes.add(fileType);
	}

	/** Sets the r, g, b and a bits per channel based on the given {@link DisplayMode} and sets the fullscreen flag to true.
	 * @param mode */
	public void setFromDisplayMode (DisplayMode mode) {
		this.width = mode.width;
		this.height = mode.height;
		if (mode.bitsPerPixel == 16) {
			this.r = 5;
			this.g = 6;
			this.b = 5;
			this.a = 0;
		}
		if (mode.bitsPerPixel == 24) {
			this.r = 8;
			this.g = 8;
			this.b = 8;
			this.a = 0;
		}
		if (mode.bitsPerPixel == 32) {
			this.r = 8;
			this.g = 8;
			this.b = 8;
			this.a = 8;
		}
		this.fullscreen = true;
	}

    /** 
     * Gets maximum number of threads to use for network requests.
     * @return {@link Integer#MAX_VALUE} by default
     */
    public int getMaxNetThreads() {
        return maxNetThreads;
    }

    /**
     * Sets maximum number of threads to use for network requests.
     * @param maxNetThreads number of threads
     */
    public void setMaxNetThreads(int maxNetThreads) {
        this.maxNetThreads = maxNetThreads;
    }

    /**
     * Whether to attempt use OpenGL ES 3.0.
     */
    public boolean isUseGL30() {
        return useGL30;
    }

    /**
     * Sets whether to attempt use OpenGL ES 3.0.
     * @param useGL30 
     */
    public void setUseGL30(boolean useGL30) {
        this.useGL30 = useGL30;
    }

    /**
     * Gets the OpenGL context minor version (the part after the decimal point) used to emulate OpenGL ES 3.0, 
     * when the version is not supported it will fall back to OpenGL ES 2.0 emulation. 
     * Defaults to 3.2 (major=3, minor=2).
     * Only used when {@link #useGL30} is true. OpenGL is fully compatible with OpenGL ES 3.0 since
     * version 4.3, setting the context version to a lower value might cause some features not to function properly. OSX requires 3.2 though.
     *
     * @return 3 by default
     * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">LWJGL OSX ContextAttribs note</a>
     */
    public int getGles30ContextMajorVersion() {
        return gles30ContextMajorVersion;
    }

    /**
     * Sets the OpenGL context version used to emulate OpenGL ES 3.0, 
     * when the version is not supported it will fall back to OpenGL ES 2.0 emulation. 
     * Defaults to 3.2 (major=3, minor=2).
     * Only used when {@link #useGL30} is true. OpenGL is fully compatible with OpenGL ES 3.0 since
     * version 4.3, setting the context version to a lower value might cause some features not to function properly. OSX requires 3.2 though.
     *
     * @param gles30ContextMajorVersion the major part of the version number
     * @param gles30ContextMinorVersion the minor part of the version number
     * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">LWJGL OSX ContextAttribs note</a>
     */
    public void setGles30ContextVersion(int gles30ContextMajorVersion , int gles30ContextMinorVersion) {
        this.gles30ContextMajorVersion = gles30ContextMajorVersion;
    }

    /** 
     * Gets the OpenGL context major version (the part in front of the decimal point) used to emulate OpenGL ES 3.0, when the version
     * is not supported it will fall back to OpenGL ES 2.0 emulation. Defaults to 3.2 (major=3, minor=2). Only used when
     * {@link #useGL30} is true. OpenGL is fully compatible with OpenGL ES 3.0 since version 4.3, setting the context version to a
     * lower value might cause some features not to function properly. OSX requires 3.2 though.
     * @return 2 by default
     * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">LWJGL OSX ContextAttribs note</a> */
    public int getGles30ContextMinorVersion() {
        return gles30ContextMinorVersion;
    }

    /** 
     * Gets the number of bits per red colour channel
     * @return 8 by default
     **/
    public int getRed() {
        return r;
    }

    /**
     * Sets the number of bits per red colour channel
     * @param r 
     */
    public void setRed(int r) {
        this.r = r;
    }

    /** 
     * Gets the number of bits per green colour channel
     * @return 8 by default
     **/
    public int getGreen() {
        return g;
    }

    /**
     * Sets the number of bits per green colour channel
     * @param g
     */
    public void setGreen(int g) {
        this.g = g;
    }

    /** 
     * Gets the number of bits per blue colour channel
     * @return 8 by default
     **/
    public int getBlue() {
        return b;
    }

    /**
     * Sets the number of bits per blue colour channel
     * @param b
     */
    public void setBlue(int b) {
        this.b = b;
    }

    /** 
     * Gets the number of bits per alpha colour channel
     * @return 8 by default
     **/
    public int getA() {
        return a;
    }

    /**
     * Sets the number of bits per alpha colour channel
     * @param a
     */
    public void setAlpha(int a) {
        this.a = a;
    }

    /**
     * Gets the number of bits for depth buffer
     * @return 16 by default
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the number of bits for depth buffer
     * @param depth number of bits
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Gets the number of bits for stencil buffer
     * @return 0 by default
     */
    public int getStencil() {
        return stencil;
    }

    /**
     * Sets the number of bits for stencil buffer
     * @param stencil number of bits
     */
    public void setStencil(int stencil) {
        this.stencil = stencil;
    }

    /**
     * Gets the number of samples for MSAA
     * @return 0 by default
     */
    public int getSamples() {
        return samples;
    }

    /**
     * Sets the number of samples for MSAA
     */
    public void setSamples(int samples) {
        this.samples = samples;
    }

    /**
     * Gets the width of the application window
     * @return 640 by default
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the application window
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the height of the application window
     * @return 480 by default
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the application window
     * @param height 
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the x position of the application window
     * @return -1 by default
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x position of the application window
     * -1 to center the window.
     * @param x x position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the y position of the application window
     * @return -1 by default
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y position of the application window
     * -1 to center the window.
     * @param y y position
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Return true if the application will run fullscreen
     * @return 
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Sets whether that application will run fullscreen
     */
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    /**
     * Used to emulate screen densities
     * @return -1 by default
     */
    public int getOverrideDensity() {
        return overrideDensity;
    }

    /**
     * Used to emulate screen densities
     */
    public void setOverrideDensity(int overrideDensity) {
        this.overrideDensity = overrideDensity;
    }

    /**
     * Gets whether to enable vsync, can be changed at runtime via {@link Graphics#setVSync(boolean)} 
     */
    public boolean isvSyncEnabled() {
        return vSyncEnabled;
    }

    /**
     * Sets whether to enable vsync, can be changed at runtime via {@link Graphics#setVSync(boolean)}
     */
    public void setvSyncEnabled(boolean vSyncEnabled) {
        this.vSyncEnabled = vSyncEnabled;
    }

    /**
     * Gets the title of the application
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the application
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets whether to call System.exit() on tear-down. Needed for Webstarts on some versions of Mac OS X it seems
     * @return true by default
     */
    public boolean isForceExit() {
        return forceExit;
    }

    /**
     * Sets whether to call System.exit() on tear-down. Needed for Webstarts on some versions of Mac OS X it seems
     * @param forceExit 
     */
    public void setForceExit(boolean forceExit) {
        this.forceExit = forceExit;
    }

    /**
     * Gets whether the window is resizable
     * @return true by default
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Sets whether the window is resizable
     * @param resizable 
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * Sets the maximum number of sources that can be played simultaneously
     * @return 16 by default
     */
    public int getAudioDeviceSimultaneousSources() {
        return audioDeviceSimultaneousSources;
    }

    /**
     * Sets the maximum number of sources that can be played simultaneously
     * @param audioDeviceSimultaneousSources 
     */
    public void setAudioDeviceSimultaneousSources(int audioDeviceSimultaneousSources) {
        this.audioDeviceSimultaneousSources = audioDeviceSimultaneousSources;
    }

    /**
     * Gets the audio device buffer size in samples 
     * @return 512 by default
     */
    public int getAudioDeviceBufferSize() {
        return audioDeviceBufferSize;
    }

    /**
     * Sets the audio device buffer size in samples 
     * @param audioDeviceBufferSize 
     */
    public void setAudioDeviceBufferSize(int audioDeviceBufferSize) {
        this.audioDeviceBufferSize = audioDeviceBufferSize;
    }

    /**
     * Gets the audio device buffer count
     * @return 9 by default
     */
    public int getAudioDeviceBufferCount() {
        return audioDeviceBufferCount;
    }

    /**
     * Sets the audio device buffer count
     * @param audioDeviceBufferCount 
     */
    public void setAudioDeviceBufferCount(int audioDeviceBufferCount) {
        this.audioDeviceBufferCount = audioDeviceBufferCount;
    }

    /**
     * Gets the initial backgroud colour
     * @return {@link Color.BLACK} by default
     */
    public Color getInitialBackgroundColor() {
        return initialBackgroundColor;
    }

    /**
     * Sets the initial backgroud colour
     * @param initialBackgroundColor 
     */
    public void setInitialBackgroundColor(Color initialBackgroundColor) {
        this.initialBackgroundColor = initialBackgroundColor;
    }

    /**
     * Target framerate when the window is in the foreground. The CPU sleeps as needed. 
     * @return 60 by deault
     */
    public int getForegroundFPS() {
        return foregroundFPS;
    }

    /**
     * Target framerate when the window is in the foreground. The CPU sleeps as needed. 
     * @param foregroundFPS Use 0 to never sleep.
     */
    public void setForegroundFPS(int foregroundFPS) {
        this.foregroundFPS = foregroundFPS;
    }

    /**
     * Target framerate when the window is not in the foreground. The CPU sleeps as needed.
     * @return 60 by default
     */
    public int getBackgroundFPS() {
        return backgroundFPS;
    }

    /**
     * Target framerate when the window is not in the foreground. The CPU sleeps as needed.
     * @param backgroundFPS Use 0 to never sleep, -1 to not render
     */
    public void setBackgroundFPS(int backgroundFPS) {
        this.backgroundFPS = backgroundFPS;
    }

    /**
     * {@link LifecycleListener#pause()} and don't render when the window is minimized.
     * @return true by default
     */
    public boolean isPauseWhenMinimized() {
        return pauseWhenMinimized;
    }

    /**
     * {@link LifecycleListener#pause()} and don't render when the window is minimized.
     * @param pauseWhenMinimized 
     */
    public void setPauseWhenMinimized(boolean pauseWhenMinimized) {
        this.pauseWhenMinimized = pauseWhenMinimized;
    }

    /** 
     * {@link LifecycleListener#pause()} (but continue rendering) when the window is not minimized and not the foreground window.
     * To stop rendering when not the foreground window, use backgroundFPS -1. 
     * @return false by default
     **/
    public boolean isPauseWhenBackground() {
        return pauseWhenBackground;
    }

    /** 
     * {@link LifecycleListener#pause()} (but continue rendering) when the window is not minimized and not the foreground window.
     * To stop rendering when not the foreground window, use backgroundFPS -1. **/
    public void setPauseWhenBackground(boolean pauseWhenBackground) {
        this.pauseWhenBackground = pauseWhenBackground;
    }

    /**
     * Allows software OpenGL rendering if hardware acceleration was not available.
     * @see LwjglGraphics#isSoftwareMode() 
     * @return false by default
     */
    public boolean isAllowSoftwareMode() {
        return allowSoftwareMode;
    }

    /**
     * Allows software OpenGL rendering if hardware acceleration was not available.
     * @see LwjglGraphics#isSoftwareMode() 
     */ 
    public void setAllowSoftwareMode(boolean allowSoftwareMode) {
        this.allowSoftwareMode = allowSoftwareMode;
    }

    /** 
     * Preferences directory on the desktop. 
     * @return Default is ".prefs/". 
     */
    public String getPreferencesDirectory() {
        return preferencesDirectory;
    }

    /**
     * Preferences directory on the desktop. 
     * @param preferencesDirectory 
     */
    public void setPreferencesDirectory(String preferencesDirectory) {
        this.preferencesDirectory = preferencesDirectory;
    }

    /**
     * Gets the Preferences file type on the desktop.
     * @return Default is {@link FileType.External}
     */
    public FileType getPreferencesFileType() {
        return preferencesFileType;
    }

    /**
     * Sets the Preferences file type on the desktop.
     * @param preferencesFileType 
     */
    public void setPreferencesFileType(FileType preferencesFileType) {
        this.preferencesFileType = preferencesFileType;
    }

    /**
     * Callback used when trying to create a display, can handle failures.
     * @return default value is null (disabled)
     */
    public LwjglGraphics.SetDisplayModeCallback getSetDisplayModeCallback() {
        return setDisplayModeCallback;
    }

    /**
     * Callback used when trying to create a display, can handle failures,
     * @param setDisplayModeCallback  {@code null} to disable
     */
    public void setSetDisplayModeCallback(LwjglGraphics.SetDisplayModeCallback setDisplayModeCallback) {
        this.setDisplayModeCallback = setDisplayModeCallback;
    }

    /**
     * Gets whether HDPI mode on Mac OS X is enabled
     * @return false by default
     */
    public boolean isUseHDPI() {
        return useHDPI;
    }

    /**
     * Sets whether HDPI mode on Mac OS X is enabled
     * @param useHDPI 
     */
    public void setUseHDPI(boolean useHDPI) {
        this.useHDPI = useHDPI;
    }        
        
	protected static class LwjglApplicationConfigurationDisplayMode extends DisplayMode {
		protected LwjglApplicationConfigurationDisplayMode (int width, int height, int refreshRate, int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
		}
	}

	public static DisplayMode getDesktopDisplayMode () {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = genv.getDefaultScreenDevice();
		java.awt.DisplayMode mode = device.getDisplayMode();
		return new LwjglApplicationConfigurationDisplayMode(mode.getWidth(), mode.getHeight(), mode.getRefreshRate(),
			mode.getBitDepth());
	}

	public static DisplayMode[] getDisplayModes () {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = genv.getDefaultScreenDevice();
		java.awt.DisplayMode desktopMode = device.getDisplayMode();
		java.awt.DisplayMode[] displayModes = device.getDisplayModes();
		ArrayList<DisplayMode> modes = new ArrayList<DisplayMode>();
		for (java.awt.DisplayMode mode : displayModes) {
			boolean duplicate = false;
			for (int i = 0; i < modes.size(); i++) {
				if (modes.get(i).width == mode.getWidth() && modes.get(i).height == mode.getHeight()
					&& modes.get(i).bitsPerPixel == mode.getBitDepth()) {
					duplicate = true;
					break;
				}
			}
			if (duplicate) continue;
			if (mode.getBitDepth() != desktopMode.getBitDepth()) continue;
			modes.add(new LwjglApplicationConfigurationDisplayMode(mode.getWidth(), mode.getHeight(), mode.getRefreshRate(),
				mode.getBitDepth()));
		}

		return modes.toArray(new DisplayMode[modes.size()]);
	}
}
