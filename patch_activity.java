    private SeekBar brightnessSeekBar;

    // Inside onCreate:
    // brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
    // brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    //     @Override
    //     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    //         WindowManager.LayoutParams lp = getWindow().getAttributes();
    //         lp.screenBrightness = progress / 255.0f;
    //         getWindow().setAttributes(lp);
    //     }
    //     @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    //     @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    // });
