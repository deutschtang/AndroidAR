package com.nthcode.mycamera;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AR2Activity extends Activity
{
    SurfaceView  cameraPreview;
    SurfaceHolder previewHolder;
    Camera camera;
    boolean inPreview;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        inPreview = false;

        cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void onResume()
    {
        super.onResume();

        camera = Camera.open();
    }

    public void onPause()
    {
        if (inPreview)
        {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) 
    {
        Camera.Size result = null;

        for(Camera.Size size : parameters.getSupportedPreviewSizes())
        {
            if (size.width<=width && size.height <= height)
            {
                if (result == null) {
                    result = size;
                }
                else
                {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceDestroyed(SurfaceHolder holder) 
        {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height, parameters);

            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                inPreview = true;
            }
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch(Throwable t) {
                Log.e("MyCamera", "Exception in setPreviewDisplay()", t);
            }
        }
    };
    
}
