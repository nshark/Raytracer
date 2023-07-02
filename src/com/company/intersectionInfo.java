package com.company;

public record intersectionInfo(double closest_t, renderable collider) {
    public intersectionInfo(double closest_t, renderable collider){
        this.closest_t = closest_t;
        this.collider = collider;
    }

}
