package net.gsantner.markor.format.asciidoc;
/*#######################################################
 *
 *   Maintained by Gregor Santner, 2018-
 *   https://gsantner.net/
 *
 *   License of this file: Apache 2.0 (Commercial upon request)
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import net.gsantner.markor.frontend.textview.SyntaxHighlighterBase;
import net.gsantner.markor.model.AppSettings;

import java.util.regex.Pattern;

import other.writeily.format.markdown.WrMarkdownHeaderSpanCreator;

public class AsciidocSyntaxHighlighter extends SyntaxHighlighterBase {

    // check on https://regex101.com/
    // the syntax patterns are simplified
    // WARNING: wrong or invalid patterns causes the app to crash, when a file opens!

    // Monospace syntax (`) must be the outermost formatting pair (i.e., outside the
    // bold formatting pair).
    // Italic syntax (_) is always the innermost formatting pair.

    // simplified, OK for basic examples
    public final static Pattern BOLD = Pattern
            .compile(
                    "(?m)(\\*\\S(?!\\*)(.*?)\\S\\*(?!\\*))");
    // simplified, OK for basic examples
    public final static Pattern ITALICS = Pattern.compile(
            "(?m)(_\\S(?!_)(.*?)\\S_(?!_))");
    // simplified, OK for basic examples, contains only inline code
    public final static Pattern SUBSCRIPT = Pattern.compile("(?m)(~(?!~)(.*?)~(?!~))");
    public final static Pattern SUPERSCRIPT = Pattern.compile("(?m)(\\^(?!\\^)(.*?)\\^(?!\\^))");
    public final static Pattern MONOSPACE = Pattern
            .compile(
                    "(?m)(`(?!`)(.*?)`(?!`))");

    public final static Pattern HEADING_SIMPLE = Pattern.compile("(?m)^(={1,6} {1}\\S.*$)");
    // simplified syntax: In fact, leading spaces are also possible

    public final static Pattern LIST_ORDERED = Pattern.compile("(?m)^(\\.{1,6})( {1})");
    public final static Pattern LIST_UNORDERED = Pattern.compile(
            "(?m)^\\*{1,6}( \\[[ xX]\\]){0,1} {1}");
    public final static Pattern LIST_DESCRIPTION = Pattern.compile("(?m)^(.+\\S(:{2,4}|;{2,2}))( {1}|[\\r\\n])");
    // TODO: use later for highlighting checklists.
    // public final static Pattern LIST_CHECKLIST = Pattern.compile("^\\*{1,6}( \\[[ xX]\\]) {1}");

    public final static Pattern ATTRIBUTE_DEFINITION = Pattern.compile("(?m)^:\\S+:");
    public final static Pattern ATTRIBUTE_REFERENCE = Pattern.compile("(?m)\\{\\S+\\}");
    public final static Pattern LINE_COMMENT = Pattern.compile("(?m)^\\/{2}(?!\\/).*$");
    public final static Pattern ADMONITION = Pattern.compile(
            "(?m)^(NOTE: |TIP: |IMPORTANT: |CAUTION: |WARNING: )");


    public final static Pattern SQUAREBRACKETS = Pattern.compile("\\[([^\\[]*)\\]");
    //TODO: issues with:
    // The *[red]##c##[green]##o##[purple]##l##[fuchsia]##o##[blue]##r##* brings contrast to the text.
    public final static Pattern HIGHLIGHT = Pattern.compile(
            "(?m)(?<!])((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))");
    public final static Pattern ROLE_GENERAL = Pattern
            .compile("(?m)\\[([^\\[]*)\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))");

    public final static Pattern ROLE_UNDERLINE = Pattern
            .compile("(?m)\\[\\.underline\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))");
    public final static Pattern ROLE_STRIKETHROUGH = Pattern
            .compile("(?m)\\[\\.line-through\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))");

    public final static Pattern HARD_LINE_BREAK = Pattern.compile(
            "(?m)(?<=\\S)([^\\S\\r\\n]{1})\\+[\\r\\n]");

    // simplified, OK for basic examples
    public final static Pattern LINK_PATTERN = Pattern.compile("link:\\S*?\\[([^\\[]*)\\]");
    public final static Pattern XREF_PATTERN = Pattern.compile("xref:\\S*?\\[([^\\[]*)\\]");
    public final static Pattern IMAGE_PATTERN = Pattern.compile("image:\\S*?\\[([^\\[]*)\\]");
    public final static Pattern INCLUDE_PATTERN = Pattern.compile("include:\\S*?\\[([^\\[]*)\\]");

    public final static Pattern BLOCKTITLE = Pattern.compile("(?m)^\\.[^(\\s|\\.)].*$");

    // block syntax
    // simplified, contains only the most common case, like "____", "....", "----", ...
    public final static Pattern BLOCK_DELIMITED_QUOTATION = Pattern.compile(
            "(?m)^\\_{4}[\\r\\n]([\\s\\S]+?(?=^\\_{4}[\\r\\n]))^\\_{4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_EXAMPLE = Pattern.compile(
            "(?m)^\\={4}[\\r\\n]([\\s\\S]+?(?=^\\={4}[\\r\\n]))^\\={4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_LISTING = Pattern.compile(
            "(?m)^\\-{4}[\\r\\n]([\\s\\S]+?(?=^\\-{4}[\\r\\n]))^\\-{4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_LITERAL = Pattern.compile(
            "(?m)^\\.{4}[\\r\\n]([\\s\\S]+?(?=^\\.{4}[\\r\\n]))^\\.{4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_SIDEBAR = Pattern.compile(
            "(?m)^\\*{4}[\\r\\n]([\\s\\S]+?(?=^\\*{4}[\\r\\n]))^\\*{4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_COMMENT = Pattern.compile(
            "(?m)^\\/{4}[\\r\\n]([\\s\\S]+?(?=^\\/{4}[\\r\\n]))^\\/{4}[\\r\\n]");
    public final static Pattern BLOCK_DELIMITED_TABLE = Pattern.compile(
            "(?m)^\\|\\={3}[\\r\\n]([\\s\\S]+?(?=^\\|\\={3}[\\r\\n]))^\\|={3}[\\r\\n]");

    // original
    // issues with content, created in Windows and directly copied to android
    // public final static Pattern DOUBLESPACE_LINE_ENDING = Pattern.compile("(?m)(?<=\\S)([^\\S\\n]{2,})\\n");
    // corrected:
    public final static Pattern DOUBLESPACE_LINE_ENDING = Pattern.compile(
            "(?m)(?<=\\S)([^\\S\\r\\n]{2,})[\\r\\n]");
//
//    // TODO: still markdown, but it is not used
//    public final static Pattern ACTION_LINK_PATTERN = Pattern.compile("(?m)\\[(.*?)\\]\\((.*?)\\)");
//

    /*
https://personal.sron.nl/~pault/[Paul Tol's Notes, Colour schemes and templates, 18 August 2021]

= INTRODUCTION TO COLOUR SCHEMES

distinct for all people, including colour-blind readers;

#default# colour scheme for qualitative data is the _bright_ scheme in https://personal.sron.nl/~pault/#fig:scheme_bright[Fig. 1]

image::https://personal.sron.nl/~pault/images/scheme_bright.png[Figure 1]

Colours in default order: '#4477AA', '#EE6677', '#228833', '#CCBB44', '#66CCEE', '#AA3377', '#BBBBBB'.

BLUE, CYAN, GREEN, YELLOW, RED, PURPLE, GRAY

blue, cyan, green, yellow, red, purple, gray

Figure 6: #_Pale_ and _dark_# qualitative colour schemes where the colours are not very distinct in either normal or colour-blind vision;
they are not meant for lines or maps, #but for marking text#. Use the *pale* colours for the *background of black text*,
for example to highlight cells in a table.
One of the *dark colours* can be chosen *for text itself on a white background*,
for example when a large block of text has to be marked.
In both cases, the text remains easily readable (see https://personal.sron.nl/~pault/#fig:orbits[Fig. 10]).

image::https://personal.sron.nl/~pault/images/scheme_pale.png[]

Colours: '#BBCCEE', '#CCEEFF', '#CCDDAA', '#EEEEBB', '#FFCCCC', '#DDDDDD'.


image:https://personal.sron.nl/~pault/images/scheme_dark.png[Dark scheme]

Colours: '#222255', '#225555', '#225522', '#666633', '#663333', '#555555'.



TODO: test on dark and black theme, maybe need to adapt
white text on areas with changed background is hard to read
use explicit text color, when background changes?

*/

    private static final int TOL_BLUE = Color.parseColor("#4477AA");
    private static final int TOL_CYAN = Color.parseColor("#EE6677");
    private static final int TOL_GREEN = Color.parseColor("#228833");
    private static final int TOL_YELLOW = Color.parseColor("#CCBB44");
    private static final int TOL_RED = Color.parseColor("#EE6677");
    private static final int TOL_PURPLE = Color.parseColor("#AA3377");
    private static final int TOL_GRAY = Color.parseColor("#BBBBBB");

    private static final int TOL_PALE_BLUE = Color.parseColor("#BBCCEE");
    private static final int TOL_PALE_CYAN = Color.parseColor("#CCEEFF");
    private static final int TOL_PALE_GREEN = Color.parseColor("#CCDDAA");
    private static final int TOL_PALE_YELLOW = Color.parseColor("#EEEEBB");
    private static final int TOL_PALE_RED = Color.parseColor("#FFCCCC");
    private static final int TOL_PALE_GRAY = Color.parseColor("#DDDDDD");

    private static final int TOL_DARK_BLUE = Color.parseColor("#222255");
    private static final int TOL_DARK_CYAN = Color.parseColor("#225555");
    private static final int TOL_DARK_GREEN = Color.parseColor("#225522");
    private static final int TOL_DARK_YELLOW = Color.parseColor("#666633");
    private static final int TOL_DARK_RED = Color.parseColor("#663333");
    private static final int TOL_DARK_GRAY = Color.parseColor("#555555");

    private static final int AD_COLOR_HEADING = TOL_BLUE;
    private static final int AD_COLOR_LINK = TOL_BLUE;
    private static final int AD_COLOR_LIST = TOL_GREEN;
    private static final int AD_COLOR_LIST_DESCRIPTION = TOL_CYAN;
    private static final int AD_COLOR_UNDERLINE_ROLE_UNDERLINE = TOL_GRAY;
    private static final int AD_COLOR_ROLE_GENERAL = TOL_PURPLE;
    private static final int AD_COLOR_ADMONITION = TOL_RED;
//    private static final int AD_COLOR_ATTRIBUTE = TOL_CYAN;

    private static final int AD_COLORBACK_LIGHT_MONOSPACE = TOL_PALE_GRAY;
    private static final int AD_COLORBACK_DARK_MONOSPACE = TOL_DARK_GRAY;
    private static final int AD_COLORBACK_LIGHT_QUOTE = TOL_PALE_GREEN;
    private static final int AD_COLORBACK_DARK_QUOTE = TOL_DARK_GREEN;
    private static final int AD_COLORBACK_LIGHT_EXAMPLE = TOL_PALE_BLUE;
    private static final int AD_COLORBACK_DARK_EXAMPLE = TOL_DARK_BLUE;
    private static final int AD_COLORBACK_LIGHT_SIDEBAR = TOL_PALE_RED;
    private static final int AD_COLORBACK_DARK_SIDEBAR = TOL_DARK_RED;
    private static final int AD_COLORBACK_LIGHT_TABLE = TOL_PALE_YELLOW;
    private static final int AD_COLORBACK_DARK_TABLE = TOL_PALE_YELLOW;
    private static final int AD_COLORBACK_LIGHT_HIGHLIGHT = Color.YELLOW;
    private static final int AD_COLORBACK_DARK_HIGHLIGHT = Color.YELLOW;
//    private static final int AD_COLORBACK_LIGHT_COMMENT = TOL_PALE_GRAY;
//    private static final int AD_COLORBACK_DARK_COMMENT = TOL_DARK_GRAY;
    private static final int AD_COLORBACK_LIGHT_ATTRIBUTE = TOL_PALE_CYAN;
    private static final int AD_COLORBACK_DARK_ATTRIBUTE = TOL_DARK_CYAN;

    private static final int AD_COLORBACK_LIGHT_SQUAREBRACKETS = TOL_PALE_GRAY;
    private static final int AD_COLORBACK_DARK_SQUAREBRACKETS = TOL_DARK_GRAY;
    private static final int AD_COLORBACK_LIGHT_BLOCKTITLE = TOL_PALE_GRAY;
    private static final int AD_COLORBACK_DARK_BLOCKTITLE = TOL_DARK_GRAY;

    // TODO: consider different AD_COLOR_TEXT_ON_COLORBACKGROUND instead of only one
    // TODO: or use _isDarkMode
//    private static final int AD_COLOR_TEXT_ON_COLORBACKGROUND = TOL_DARK_GRAY;
    private static final int AD_COLOR_COMMENT = TOL_GRAY;
    private static final int AD_COLOR_HIGHLIGHT = Color.BLACK;


    private boolean _highlightLineEnding;
    private boolean _highlightCodeChangeFont;
    private boolean _highlightBiggerHeadings;
    private boolean _highlightCodeBlock;


    public AsciidocSyntaxHighlighter(AppSettings as) {
        super(as);
    }

    @Override
    public SyntaxHighlighterBase configure(Paint paint) {
        _highlightLineEnding = _appSettings.isAsciidocHighlightLineEnding();
// TODO: does not work yet
        _highlightBiggerHeadings = _appSettings.isAsciidocBiggerHeadings();
        _highlightCodeChangeFont = _appSettings.isHighlightCodeMonospaceFont();
        _highlightCodeBlock = _appSettings.isHighlightCodeBlock();
        _delay = _appSettings.getAsciidocHighlightingDelay();
        return super.configure(paint);
    }

    @Override
    protected void generateSpans() {
        createTabSpans(_tabSize);
        // TODO: understand: what happens here?
        createUnderlineHexColorsSpans();
        // TODO: createSmallBlueLinkSpans() - font is very small, currently general setting: 85% of common size
        // hard to read on dark theme, but this is a general question for all formats,
        // not AsciiDoc specific
        // also it uses private static String formatLink(String text, String link), which is
        // adapted for Markdown
        // maybe, needs to be adapted for AsciiDoc?
        // but not in the current Pull Request
        // createSmallBlueLinkSpans();

        //TODO: doesn't yet work, but it is called
        if (_highlightBiggerHeadings) {
            createSpanForMatches(HEADING_SIMPLE,
                    new WrMarkdownHeaderSpanCreator(_spannable, AD_COLOR_HEADING, _textSize));
        } else {
            createColorSpanForMatches(HEADING_SIMPLE, AD_COLOR_HEADING);
        }

        if (_highlightCodeChangeFont) {
            createMonospaceSpanForMatches(MONOSPACE);
            createMonospaceSpanForMatches(BLOCK_DELIMITED_LISTING);
            createMonospaceSpanForMatches(BLOCK_DELIMITED_LITERAL);
            createMonospaceSpanForMatches(LIST_UNORDERED);
            createMonospaceSpanForMatches(LIST_ORDERED);
            createMonospaceSpanForMatches(ATTRIBUTE_DEFINITION);
            createMonospaceSpanForMatches(ADMONITION);
        }

        createStyleSpanForMatches(BOLD, Typeface.BOLD);
        createStyleSpanForMatches(ITALICS, Typeface.ITALIC);

        createColorSpanForMatches(LINK_PATTERN, AD_COLOR_LINK);
        createColorSpanForMatches(XREF_PATTERN, AD_COLOR_LINK);
        createColorSpanForMatches(IMAGE_PATTERN, AD_COLOR_LINK);
        createColorSpanForMatches(INCLUDE_PATTERN, AD_COLOR_LINK);

        createColorSpanForMatches(LIST_UNORDERED, AD_COLOR_LIST);
        createColorSpanForMatches(LIST_ORDERED, AD_COLOR_LIST);
        createColorSpanForMatches(LIST_DESCRIPTION, AD_COLOR_LIST_DESCRIPTION);
        //TODO: test for interfernce with other role like underline, line-through => interference
        createColorSpanForMatches(ROLE_GENERAL, AD_COLOR_ROLE_GENERAL);
//        createColorSpanForMatches(ATTRIBUTE_DEFINITION, AD_COLOR_ATTRIBUTE);

        createStyleSpanForMatches(ADMONITION, Typeface.BOLD);
        createColorSpanForMatches(ADMONITION, AD_COLOR_ADMONITION);

        createColorBackgroundSpan(SQUAREBRACKETS, _isDarkMode ? AD_COLORBACK_DARK_SQUAREBRACKETS : AD_COLORBACK_LIGHT_SQUAREBRACKETS);
        createColorBackgroundSpan(BLOCKTITLE, _isDarkMode ? AD_COLORBACK_DARK_BLOCKTITLE : AD_COLORBACK_LIGHT_BLOCKTITLE);


        if (_highlightLineEnding) {
            createColorBackgroundSpan(HARD_LINE_BREAK, _isDarkMode ? AD_COLORBACK_DARK_MONOSPACE : AD_COLORBACK_LIGHT_MONOSPACE);
//            //test markdown original pattern, same issues, when content is created in windows
//            createColorBackgroundSpan(DOUBLESPACE_LINE_ENDING, AD_COLOR_CODEBLOCK);
        }

        if (_highlightCodeBlock) {
            createColorBackgroundSpan(MONOSPACE, _isDarkMode ? AD_COLORBACK_DARK_MONOSPACE : AD_COLORBACK_LIGHT_MONOSPACE);
//            createColorSpanForMatches(CODE, AD_COLOR_TEXT_ON_COLORBACKGROUND);
            createColorBackgroundSpan(BLOCK_DELIMITED_LISTING, _isDarkMode ? AD_COLORBACK_DARK_MONOSPACE
                    : AD_COLORBACK_LIGHT_MONOSPACE);
            createColorBackgroundSpan(BLOCK_DELIMITED_LITERAL, _isDarkMode ? AD_COLORBACK_DARK_MONOSPACE
                    : AD_COLORBACK_LIGHT_MONOSPACE);
            createColorBackgroundSpan(BLOCK_DELIMITED_QUOTATION, _isDarkMode ? AD_COLORBACK_DARK_QUOTE : AD_COLORBACK_LIGHT_QUOTE);
            createColorBackgroundSpan(BLOCK_DELIMITED_EXAMPLE, _isDarkMode ? AD_COLORBACK_DARK_EXAMPLE : AD_COLORBACK_LIGHT_EXAMPLE);
            createColorBackgroundSpan(BLOCK_DELIMITED_SIDEBAR, _isDarkMode ? AD_COLORBACK_DARK_SIDEBAR : AD_COLORBACK_LIGHT_SIDEBAR);
            createColorBackgroundSpan(BLOCK_DELIMITED_TABLE, _isDarkMode ? AD_COLORBACK_DARK_TABLE : AD_COLORBACK_LIGHT_TABLE);
//            createColorSpanForMatches(BLOCK_DELIMITED_SIDEBAR, AD_COLOR_TEXT_ON_COLORBACKGROUND);

//            createColorBackgroundSpan(BLOCK_DELIMITED_COMMENT, _isDarkMode ? AD_COLORBACK_DARK_COMMENT : AD_COLORBACK_LIGHT_COMMENT);
            createColorSpanForMatches(BLOCK_DELIMITED_COMMENT, AD_COLOR_COMMENT);
        }

//        createColorBackgroundSpan(LINE_COMMENT, _isDarkMode ? AD_COLORBACK_DARK_COMMENT : AD_COLORBACK_LIGHT_COMMENT);
        createColorSpanForMatches(LINE_COMMENT, AD_COLOR_COMMENT);
        createColorBackgroundSpan(HIGHLIGHT, _isDarkMode ? AD_COLORBACK_DARK_HIGHLIGHT : AD_COLORBACK_LIGHT_HIGHLIGHT);
        createColorSpanForMatches(HIGHLIGHT, AD_COLOR_HIGHLIGHT);
        createColorBackgroundSpan(ATTRIBUTE_DEFINITION, _isDarkMode ? AD_COLORBACK_DARK_ATTRIBUTE : AD_COLORBACK_LIGHT_ATTRIBUTE);
        createColorBackgroundSpan(ATTRIBUTE_REFERENCE, _isDarkMode ? AD_COLORBACK_DARK_ATTRIBUTE : AD_COLORBACK_LIGHT_ATTRIBUTE);

        createSubscriptStyleSpanForMatches(SUBSCRIPT);
        createSuperscriptStyleSpanForMatches(SUPERSCRIPT);
        createStrikeThroughSpanForMatches(ROLE_STRIKETHROUGH);
        // TODO: ist nur ganz dünn und kaum zu sehen
        createColoredUnderlineSpanForMatches(ROLE_UNDERLINE, AD_COLOR_UNDERLINE_ROLE_UNDERLINE);

    }
}

