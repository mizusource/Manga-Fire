        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
                float brightness = progress / 255.0f;
                if (brightness < 0.05f) brightness = 0.05f; // Prevent completely black screen
                lp.screenBrightness = brightness;
                getWindow().setAttributes(lp);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
