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

package com.badlogic.gdx.files;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.Files.FileType;

/** A FileHandle intended to be subclassed for the purpose of implementing {@link #read()} and/or {@link #write(boolean)}. Methods
 * that would manipulate the file instead throw UnsupportedOperationException.
 * @author Nathan Sweet */
public abstract class FileHandleStream extends FileHandle {
	/** Create an {@link FileType#Absolute} file at the given location. */
	public FileHandleStream (String path) {
		super(new File(path), FileType.Absolute);
	}

        @Override
	public boolean isDirectory () {
		return false;
	}

        @Override
	public long length () {
		return 0;
	}

        @Override
	public boolean exists () {
		return true;
	}

        @Override
	public FileHandle child (String name) {
		throw new UnsupportedOperationException();
	}

        @Override
	public FileHandle sibling (String name) {
		throw new UnsupportedOperationException();
	}

        @Override
	public FileHandle parent () {
		throw new UnsupportedOperationException();
	}

        @Override
	public InputStream read () {
		throw new UnsupportedOperationException();
	}

        @Override
	public OutputStream write (boolean overwrite) {
		throw new UnsupportedOperationException();
	}

        @Override
	public FileHandle[] list () {
		throw new UnsupportedOperationException();
	}

        @Override
	public void mkdirs () {
		throw new UnsupportedOperationException();
	}

        @Override
	public boolean delete () {
		throw new UnsupportedOperationException();
	}

        @Override
	public boolean deleteDirectory () {
		throw new UnsupportedOperationException();
	}

        @Override
	public void copyTo (FileHandle dest) {
		throw new UnsupportedOperationException();
	}

        @Override
	public void moveTo (FileHandle dest) {
		throw new UnsupportedOperationException();
	}
}
