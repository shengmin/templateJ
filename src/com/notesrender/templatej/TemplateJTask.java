/**
 * Copyright (c) 2011 Shengmin Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.notesrender.templatej;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public final class TemplateJTask extends Task {
	private String _nsPrefix;
	private ArrayList<FileSet> _filesets = new ArrayList<FileSet>();

	public void setPrefix(String prefix){ _nsPrefix = prefix;}
	public void addFileSet(FileSet fileset){
		_filesets.add(fileset);		
	}

	@Override
	public void execute(){
		int size = _filesets.size();
		TemplateProcessor processor = new TemplateProcessor(_nsPrefix);
		for(int i=0; i<size;i++){
			FileSet fs = _filesets.get(i);
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			File dir = ds.getBasedir();
			String[] srcs = ds.getIncludedFiles();

			for (int j = 0; j < srcs.length; j++) {                   
				File temp = new File(dir,srcs[j]);
				String absolutePath = temp.getAbsolutePath();
				processor.process(absolutePath);

			}
		}


	}
}
