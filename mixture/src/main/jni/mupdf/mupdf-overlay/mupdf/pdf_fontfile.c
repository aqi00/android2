#include "fitz.h"
#include "mupdf.h"

enum
{
	FD_FIXED = 1 << 0,
	FD_SERIF = 1 << 1,
	FD_SYMBOLIC = 1 << 2,
	FD_SCRIPT = 1 << 3,
	FD_NONSYMBOLIC = 1 << 5,
	FD_ITALIC = 1 << 6,
	FD_ALLCAP = 1 << 16,
	FD_SMALLCAP = 1 << 17,
	FD_FORCEBOLD = 1 << 18
};

extern fz_error
pdf_getfontfile(pdf_fontdesc *font, char *fontname, char *collection, char **filename);

fz_error
pdf_loadstoredfont(pdf_fontdesc *font, char *fontname, char *collection)
{
	fz_error error;
	unsigned char *data;
	unsigned int len;
	char *filename = NULL;

	error = pdf_getfontbuffer(font, fontname, collection, &data, &len);
	if (error)
		goto trycid;

	pdf_logfont("load builtin font %s\n", fontname);

	error = fz_newfontfrombuffer(&font->font, data, len, 0);
	if (error)
		return fz_rethrow(error, "cannot load freetype font from buffer");

	return fz_okay;

trycid:
	error = pdf_getfontfile(font, fontname, collection, &filename);
	if (error)
		return fz_rethrow(error, "cannot get filename for font");

	error = fz_newfontfromfile(&font->font, filename, 0);

	if(filename != NULL)
		fz_free(filename);

	if (error)
		return fz_rethrow(error, "cannot load font from file");

	return fz_okay;
}

fz_error
pdf_loadbuiltinfont(pdf_fontdesc *font, char *basefont)
{
	return pdf_loadstoredfont(font, basefont, "");
}

fz_error
pdf_loadsystemfont(pdf_fontdesc *font, char *fontname, char *collection)
{
	fz_error error;
	char *name;

	int isbold = 0;
	int isitalic = 0;
	int isserif = 0;
	int isscript = 0;
	int isfixed = 0;

	if (strstr(fontname, "Bold"))
		isbold = 1;
	if (strstr(fontname, "Italic"))
		isitalic = 1;
	if (strstr(fontname, "Oblique"))
		isitalic = 1;

	if (font->flags & FD_FIXED)
		isfixed = 1;
	if (font->flags & FD_SERIF)
		isserif = 1;
	if (font->flags & FD_ITALIC)
		isitalic = 1;
	if (font->flags & FD_SCRIPT)
		isscript = 1;
	if (font->flags & FD_FORCEBOLD)
		isbold = 1;

	pdf_logfont("fixed-%d serif-%d italic-%d script-%d bold-%d\n",
		isfixed, isserif, isitalic, isscript, isbold);

	if (collection)
	{
		return pdf_loadstoredfont(font, "CID-Substitute", collection);
	}

	if (isscript)
		name = "Chancery";

	else if (isfixed)
	{
		if (isitalic) {
			if (isbold) name = "Courier-BoldOblique";
			else name = "Courier-Oblique";
		}
		else {
			if (isbold) name = "Courier-Bold";
			else name = "Courier";
		}
	}

	else if (isserif)
	{
		if (isitalic) {
			if (isbold) name = "Times-BoldItalic";
			else name = "Times-Italic";
		}
		else {
			if (isbold) name = "Times-Bold";
			else name = "Times-Roman";
		}
	}

	else
	{
		if (isitalic) {
			if (isbold) name = "Helvetica-BoldOblique";
			else name = "Helvetica-Oblique";
		}
		else {
			if (isbold) name = "Helvetica-Bold";
			else name = "Helvetica";
		}
	}

	error = pdf_loadbuiltinfont(font, name);
	if (error)
		return fz_throw("cannot load builtin substitute font: %s", name);

	/* it's a substitute font: override the metrics */
	font->font->ftsubstitute = 1;

	return fz_okay;
}

fz_error
pdf_loadembeddedfont(pdf_fontdesc *font, pdf_xref *xref, fz_obj *stmref)
{
	fz_error error;
	fz_buffer *buf;

	pdf_logfont("load embedded font\n");

	error = pdf_loadstream(&buf, xref, fz_tonum(stmref), fz_togen(stmref));
	if (error)
		return fz_rethrow(error, "cannot load font stream");

	error = fz_newfontfrombuffer(&font->font, buf->rp, buf->wp - buf->rp, 0);
	if (error)
	{
		fz_dropbuffer(buf);
		return fz_rethrow(error, "cannot load embedded font (%d %d R)", fz_tonum(stmref), fz_togen(stmref));
	}

	font->buffer = buf->rp; /* save the buffer so we can free it later */
	fz_free(buf); /* only free the fz_buffer struct, not the contained data */

	font->isembedded = 1;

	return fz_okay;
}

