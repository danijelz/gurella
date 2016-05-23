#ifdef GL_ES
precision mediump float;
#endif

const vec4 WIRE_COLOR = vec4(1, 0.451, 0, 1.0);

void main(void) {
    gl_FragColor = WIRE_COLOR;
}