package com.seregamorph.restapi.utils;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@SuppressWarnings("unused")
class TestClassWithDifferentAccessors {

    private String privateProperty;
    private String protectedProperty;
    private String packageProtectedProperty;
    private String privateGetter;
    private String protectedGetter;
    private String packageProtectedGetter;
    private String privateSetter;
    private String protectedSetter;
    private String packageProtectedSetter;

    private String getPrivateProperty() {
        return privateProperty;
    }

    private void setPrivateProperty(String privateProperty) {
        this.privateProperty = privateProperty;
    }

    protected String getProtectedProperty() {
        return protectedProperty;
    }

    protected void setProtectedProperty(String protectedProperty) {
        this.protectedProperty = protectedProperty;
    }

    String getPackageProtectedProperty() {
        return packageProtectedProperty;
    }

    void setPackageProtectedProperty(String packageProtectedProperty) {
        this.packageProtectedProperty = packageProtectedProperty;
    }

    private String getPrivateGetter() {
        return privateGetter;
    }

    public void setPrivateGetter(String privateGetter) {
        this.privateGetter = privateGetter;
    }

    protected String getProtectedGetter() {
        return protectedGetter;
    }

    public void setProtectedGetter(String protectedGetter) {
        this.protectedGetter = protectedGetter;
    }

    String getPackageProtectedGetter() {
        return packageProtectedGetter;
    }

    public void setPackageProtectedGetter(String packageProtectedGetter) {
        this.packageProtectedGetter = packageProtectedGetter;
    }

    public String getPrivateSetter() {
        return privateSetter;
    }

    private void setPrivateSetter(String privateSetter) {
        this.privateSetter = privateSetter;
    }

    public String getProtectedSetter() {
        return protectedSetter;
    }

    protected void setProtectedSetter(String protectedSetter) {
        this.protectedSetter = protectedSetter;
    }

    public String getPackageProtectedSetter() {
        return packageProtectedSetter;
    }

    void setPackageProtectedSetter(String packageProtectedSetter) {
        this.packageProtectedSetter = packageProtectedSetter;
    }
}
