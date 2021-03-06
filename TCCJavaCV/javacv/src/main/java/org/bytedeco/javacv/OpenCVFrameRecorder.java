/*
 * Copyright (C) 2009-2012 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacv;

import java.io.File;
import org.bytedeco.javacpp.Loader;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_videoio.*;

/**
 *
 * @author Samuel Audet
 */
public class OpenCVFrameRecorder extends FrameRecorder {
    public static OpenCVFrameRecorder createDefault(File f, int w, int h)   throws Exception { return new OpenCVFrameRecorder(f, w, h); }
    public static OpenCVFrameRecorder createDefault(String f, int w, int h) throws Exception { return new OpenCVFrameRecorder(f, w, h); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.javacpp.opencv_highgui.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + OpenCVFrameRecorder.class, t);
            }
        }
    }

    public OpenCVFrameRecorder(File file, int imageWidth, int imageHeight) {
        this(file.getAbsolutePath(), imageWidth, imageHeight);
    }
    public OpenCVFrameRecorder(String filename, int imageWidth, int imageHeight) {
        this.filename    = filename;
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;

        this.pixelFormat = 1;
        this.videoCodec  = windows ? CV_FOURCC_PROMPT : CV_FOURCC_DEFAULT;
        this.frameRate   = 30;
    }
    public void release() throws Exception {
        if (writer != null) {
            cvReleaseVideoWriter(writer);
            writer = null;
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private static final boolean windows = Loader.getPlatform().startsWith("windows");
    private String filename;
    private CvVideoWriter writer = null;
    private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    public void start() throws Exception {
        writer = cvCreateVideoWriter(filename, videoCodec, frameRate, cvSize(imageWidth, imageHeight), pixelFormat);
        if (writer == null) {
            throw new Exception("cvCreateVideoWriter(): Could not create a writer");
        }
    }

    public void stop() throws Exception {
        release();
    }

    public void record(Frame frame) throws Exception {
        IplImage image = converter.convert(frame);
        if (writer != null) {
            if (cvWriteFrame(writer, image) == 0) {
                throw new Exception("cvWriteFrame(): Could not record frame");
            }
        } else {
            throw new Exception("Cannot record: There is no writer (Has start() been called?)");
        }
        frame.keyFrame = true;
    }
}
