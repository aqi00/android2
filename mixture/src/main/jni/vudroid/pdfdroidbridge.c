#include <jni.h>

#include <android/log.h>

#include <errno.h>

#include <fitz.h>
#include <mupdf.h>

/* Debugging helper */


#define DEBUG(args...) \
	__android_log_print(ANDROID_LOG_DEBUG, "PdfDroid", args)

//#define DEBUG(args...) {}
#define ERROR(args...) \
	__android_log_print(ANDROID_LOG_ERROR, "PdfDroid", args)

#define INFO(args...) \
	__android_log_print(ANDROID_LOG_INFO, "PdfDroid", args)
//#define INFO(args...) {}

typedef struct renderdocument_s renderdocument_t;
struct renderdocument_s
{
	pdf_xref *xref;
	fz_renderer *rast;
};

typedef struct renderpage_s renderpage_t;
struct renderpage_s
{
	pdf_page *page;
};

JNI_OnLoad(JavaVM *jvm, void *reserved)
{
	DEBUG("initializing PdfRender JNI library based on MuPDF");

	/* Fitz library setup */
	fz_cpudetect();
	fz_accelerate();

	return JNI_VERSION_1_2;
}

#define RUNTIME_EXCEPTION "java/lang/RuntimeException"

void throw_exception(JNIEnv *env, char *message)
{
	jthrowable new_exception = (*env)->FindClass(env, RUNTIME_EXCEPTION);
	if(new_exception == NULL) {
		return;
	} else {
		DEBUG("Exception '%s', Message: '%s'", RUNTIME_EXCEPTION, message);
	}
	(*env)->ThrowNew(env, new_exception, message);
}


JNIEXPORT jlong JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfDocument_open
	(JNIEnv *env, jclass clazz,
			jint fitzmemory, jstring fname, jstring pwd)
{
	fz_error error;
	fz_obj *obj;
	renderdocument_t *doc;
	jboolean iscopy;
	jclass cls;
	jfieldID fid;
	char *filename;
	char *password;

	filename = (*env)->GetStringUTFChars(env, fname, &iscopy);
	password = (*env)->GetStringUTFChars(env, pwd, &iscopy);

	doc = fz_malloc(sizeof(renderdocument_t));
	if(!doc) {
		throw_exception(env, "Out of Memory");
		goto cleanup;
	}

	/* initialize renderer */

	error = fz_newrenderer(&doc->rast, pdf_devicergb, 0, (int) fitzmemory);
	if (error) {
		throw_exception(env, "Cannot create new renderer");
		goto cleanup;
	}

	/*
	 * Open PDF and load xref table
	 */

	doc->xref = pdf_newxref();
	error = pdf_loadxref(doc->xref, filename);
	if (error) {
		/* TODO: plug into fitz error handling */
		fz_catch(error, "trying to repair");
		INFO("Corrupted file '%s', trying to repair", filename);
		error = pdf_repairxref(doc->xref, filename);
		if (error) {
			throw_exception(env,
					"PDF file is corrupted");
			goto cleanup;
		}
	}

	error = pdf_decryptxref(doc->xref);
	if (error) {
		throw_exception(env,
				"Cannot decrypt XRef table");
		goto cleanup;
	}

	/*
	 * Handle encrypted PDF files
	 */

	if (pdf_needspassword(doc->xref)) {
		if(strlen(password)) {
			int ok = pdf_authenticatepassword(doc->xref, password);
			if(!ok) {
				throw_exception(env,
						"Wrong password given");
				goto cleanup;
			}
		} else {
			throw_exception(env,
					"PDF needs a password!");
			goto cleanup;
		}
	}

	/*
	 * Load document metadata (at some point this might be implemented
	 * in the muPDF lib itself)
	 */

	obj = fz_dictgets(doc->xref->trailer, "Root");
	doc->xref->root = fz_resolveindirect(obj);
	if (!doc->xref->root) {
		fz_throw("syntaxerror: missing Root object");
		throw_exception(env, "PDF syntax: missing \"Root\" object");
		goto cleanup;
	}
	fz_keepobj(doc->xref->root);

	obj = fz_dictgets(doc->xref->trailer, "Info");
	doc->xref->info = fz_resolveindirect(obj);
	if (doc->xref->info)
		fz_keepobj(doc->xref->info);

cleanup:

	(*env)->ReleaseStringUTFChars(env, fname, filename);
	(*env)->ReleaseStringUTFChars(env, pwd, password);

	DEBUG("PdfDocument.nativeOpen(): return handle = %p", doc);
	return (jlong) doc;
}

JNIEXPORT void JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfDocument_free
	(JNIEnv *env, jclass clazz, jlong handle)
{
	renderdocument_t *doc = (renderdocument_t*) handle;

	if(doc) {
		if (doc->xref->store)
			pdf_dropstore(doc->xref->store);

		pdf_closexref(doc->xref);

		fz_droprenderer(doc->rast);

		fz_free(doc);
	}
}

JNIEXPORT jint JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfDocument_getPageCount
	(JNIEnv *env, jclass clazz, jlong handle)
{
	renderdocument_t *doc = (renderdocument_t*) handle;
	return pdf_getpagecount(doc->xref);
}

JNIEXPORT jlong JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfPage_open
	(JNIEnv *env, jclass clazz, jlong dochandle, jint pageno)
{
	renderdocument_t *doc = (renderdocument_t*) dochandle;
	renderpage_t *page;
	fz_error error;
	fz_obj *obj;
	jclass cls;
	jfieldID fid;

	page = fz_malloc(sizeof(renderpage_t));
	if(!page) {
		throw_exception(env, "Out of Memory");
		return (jlong) NULL;
	}

	pdf_flushxref(doc->xref, 0);
	obj = pdf_getpageobject(doc->xref, pageno);
	error = pdf_loadpage(&page->page, doc->xref, obj);
	if (error) {
		throw_exception(env, "error loading page");
		goto cleanup;
	}

cleanup:
	/* nothing yet */

	DEBUG("PdfPage.nativeOpenPage(): return handle = %p", page);
	return (jlong) page;
}

JNIEXPORT void JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfPage_free
	(JNIEnv *env, jclass clazz, jlong handle)
{
	renderpage_t *page = (renderpage_t*) handle;
	if(page) {
		if (page->page)
			pdf_droppage(page->page);

		fz_free(page);
	}
}

JNIEXPORT void JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfPage_getMediaBox
	(JNIEnv *env, jclass clazz, jlong handle, jfloatArray mediabox)
{
	renderpage_t *page = (renderpage_t*) handle;
	jfloat *bbox = (*env)->GetPrimitiveArrayCritical(env, mediabox, 0);
	if(!bbox) return;
	bbox[0] = page->page->mediabox.x0;
	bbox[1] = page->page->mediabox.y0;
	bbox[2] = page->page->mediabox.x1;
	bbox[3] = page->page->mediabox.y1;
	(*env)->ReleasePrimitiveArrayCritical(env, mediabox, bbox, 0);
}



JNIEXPORT void JNICALL
	Java_org_vudroid_pdfdroid_codec_PdfPage_render
	(JNIEnv *env, jclass clazz, jlong dochandle, jlong pagehandle,
		jintArray viewboxarray, jfloatArray matrixarray,
		jobject byteBuffer, jobject tempBuffer)
{
	renderdocument_t *doc = (renderdocument_t*) dochandle;
	renderpage_t *page = (renderpage_t*) pagehandle;
	fz_error error;
	fz_matrix ctm;
	fz_irect viewbox;
	fz_pixmap *pixmap;
	jfloat *matrix;
	jint *viewboxarr;
	jint *dimen;
	unsigned short *targetBuffer;
	int length, val;

	pixmap = fz_malloc(sizeof(fz_pixmap));
	if(!pixmap) {
		throw_exception(env, "Out of Memory");
	}

	/* initialize parameter arrays for MuPDF */

	matrix = (*env)->GetPrimitiveArrayCritical(env, matrixarray, 0);
	ctm.a = matrix[0];
	ctm.b = matrix[1];
	ctm.c = matrix[2];
	ctm.d = matrix[3];
	ctm.e = matrix[4];
	ctm.f = matrix[5];
	(*env)->ReleasePrimitiveArrayCritical(env, matrixarray, matrix, 0);
	DEBUG("Matrix: %f %f %f %f %f %f",
			ctm.a, ctm.b, ctm.c, ctm.d, ctm.e, ctm.f);

	viewboxarr = (*env)->GetPrimitiveArrayCritical(env, viewboxarray, 0);
	viewbox.x0 = viewboxarr[0];
	viewbox.y0 = viewboxarr[1];
	viewbox.x1 = viewboxarr[2];
	viewbox.y1 = viewboxarr[3];
	(*env)->ReleasePrimitiveArrayCritical(env, viewboxarray, viewboxarr, 0);
	DEBUG("Viewbox: %d %d %d %d",
			viewbox.x0, viewbox.y0, viewbox.x1, viewbox.y1);

	/* do the rendering */
	DEBUG("doing the rendering...");

	pixmap->x = viewbox.x0;
	pixmap->y = viewbox.y0;
	pixmap->w = viewbox.x1 - viewbox.x0;
	pixmap->h = viewbox.y1 - viewbox.y0;
	pixmap->n = 4;

	DEBUG("Allocating temp buffer");
	pixmap->samples = (*env)->GetDirectBufferAddress(env, tempBuffer);
	if (!pixmap->samples)
	{
		goto cleanup;
	}

	DEBUG("Erasing temp buffer by white: %p", pixmap->samples);
	// white:
//	memset(pixmap->samples, 0xff, pixmap->w * pixmap->h * pixmap->n);

	DEBUG("RenderTreeOver rast: %p, tree: %p", doc->rast, page->page->tree);
	// do the actual rendering:
	error = fz_rendertreeover(doc->rast, pixmap, page->page->tree, ctm);
//	error = 0;

	if (error) {
		DEBUG("error!");
		throw_exception(env, "error rendering page");
	}

	DEBUG("Accessing direct buffer");

	targetBuffer = (*env)->GetDirectBufferAddress(env, byteBuffer);
	if (!targetBuffer)
	{
		goto cleanup;
	}

	/* evil magic: we transform the rendered image's byte order
	 */
	if(!error) {
		DEBUG("Converting image buffer pixel order");
		length = pixmap->w * pixmap->h;
		unsigned int *col = pixmap->samples;
		int c = 0;
		for(val = 0; val < length; val++) {
			targetBuffer[val] = (((col[val] & 0xFF000000) >> 16) & 0xF800) |
					(((col[val] & 0x00FF0000) >> 11) & 0x07E0) |
					(((col[val] & 0x0000FF00) >> 11) & 0x001E);
		}
	}

cleanup:

	fz_free(pixmap);

	DEBUG("PdfView.nativeCreateView() done");
}

JNIEXPORT void JNICALL
Java_org_vudroid_pdfdroid_codec_PdfPage_nativeCreateView
	(JNIEnv *env, jobject this, jlong dochandle, jlong pagehandle,
		jintArray viewboxarray, jfloatArray matrixarray,
		jintArray bufferarray)
{
	renderdocument_t *doc = (renderdocument_t*) dochandle;
	renderpage_t *page = (renderpage_t*) pagehandle;
	DEBUG("PdfView(%p).nativeCreateView(%p, %p)", this, doc, page);
	fz_error error;
	fz_matrix ctm;
	fz_irect viewbox;
	fz_pixmap *pixmap;
	jfloat *matrix;
	jint *viewboxarr;
	jint *dimen;
	jint *buffer;
	int length, val;

	pixmap = fz_malloc(sizeof(fz_pixmap));
	if(!pixmap) {
		throw_exception(env, "Out of Memory");
	}

	/* initialize parameter arrays for MuPDF */

	matrix = (*env)->GetPrimitiveArrayCritical(env, matrixarray, 0);
	ctm.a = matrix[0];
	ctm.b = matrix[1];
	ctm.c = matrix[2];
	ctm.d = matrix[3];
	ctm.e = matrix[4];
	ctm.f = matrix[5];
	(*env)->ReleasePrimitiveArrayCritical(env, matrixarray, matrix, 0);
	DEBUG("Matrix: %f %f %f %f %f %f",
			ctm.a, ctm.b, ctm.c, ctm.d, ctm.e, ctm.f);

	viewboxarr = (*env)->GetPrimitiveArrayCritical(env, viewboxarray, 0);
	viewbox.x0 = viewboxarr[0];
	viewbox.y0 = viewboxarr[1];
	viewbox.x1 = viewboxarr[2];
	viewbox.y1 = viewboxarr[3];
	(*env)->ReleasePrimitiveArrayCritical(env, viewboxarray, viewboxarr, 0);
	DEBUG("Viewbox: %d %d %d %d",
			viewbox.x0, viewbox.y0, viewbox.x1, viewbox.y1);

	/* do the rendering */
	DEBUG("doing the rendering...");
	buffer = (*env)->GetPrimitiveArrayCritical(env, bufferarray, 0);

	pixmap->x = viewbox.x0;
	pixmap->y = viewbox.y0;
	pixmap->w = viewbox.x1 - viewbox.x0;
	pixmap->h = viewbox.y1 - viewbox.y0;
	pixmap->n = 4;
	pixmap->samples = (void*)buffer;

	// white:
	memset(pixmap->samples, 0xff, pixmap->w * pixmap->h * pixmap->n);

	// do the actual rendering:
	error = fz_rendertreeover(doc->rast, pixmap, page->page->tree, ctm);

	/* evil magic: we transform the rendered image's byte order
	 */
	if(!error) {
		DEBUG("Converting image buffer pixel order");
		length = pixmap->w * pixmap->h;
		unsigned int *col = pixmap->samples;
		int c = 0;
		for(val = 0; val < length; val++) {
			col[val] = ((col[val] & 0xFF000000) >> 24) |
					((col[val] & 0x00FF0000) >> 8) |
					((col[val] & 0x0000FF00) << 8);
		}
	}

	(*env)->ReleasePrimitiveArrayCritical(env, bufferarray, buffer, 0);

	fz_free(pixmap);

	if (error) {
		DEBUG("error!");
		throw_exception(env, "error rendering page");
	}

	DEBUG("PdfView.nativeCreateView() done");
}
