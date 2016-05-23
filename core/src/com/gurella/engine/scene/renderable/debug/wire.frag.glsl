#ifdef GL_ES
precision mediump float;
#endif

const vec4 WIRE_COLOR = vec4(0, 0.714, 0.586, 1.0);

void main(void) {
    gl_FragColor = WIRE_COLOR;
}