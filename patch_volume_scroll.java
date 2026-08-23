    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
            recyclerView.smoothScrollBy(0, recyclerView.getHeight() / 2);
            return true;
        } else if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            recyclerView.smoothScrollBy(0, -recyclerView.getHeight() / 2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
