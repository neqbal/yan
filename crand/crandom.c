#include <jni.h>
#include <stdlib.h>
#include "RandomC.h"

JNIEXPORT jint JNICALL Java_RandomC_getRandom(JNIEnv *env, jobject obj) {
  return rand();
}
