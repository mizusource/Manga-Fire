            ListPreference themePref = findPreference("app_theme");
            if (themePref != null) {
                themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext());
                    sp.edit().putString("app_theme", (String) newValue).apply();
                    
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                });
            }
