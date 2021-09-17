package com.transsnet.transsdktest.Factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.transsnet.transsdktest.viewmodel.TestViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;

    public ViewModelFactory(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TestViewModel.class)) {
            return (T) new TestViewModel();
        }
        return null;
    }
}
