/*#######################################################
 *
 *   Maintained by Gregor Santner, 2018-
 *   https://gsantner.net/
 *
 *   License of this file: Apache 2.0 (Commercial upon request)
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package net.gsantner.markor.format.asciidoc;

import net.gsantner.markor.format.ActionButtonBase;
import net.gsantner.markor.frontend.textview.AutoTextFormatter;
import net.gsantner.markor.frontend.textview.ReplacePatternGeneratorHelper;
import net.gsantner.markor.frontend.textview.TextViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AsciidocReplacePatternGenerator {

    // TODO: write tests
    //adapted for asciidoc

    //check on https://regex101.com/
    // in Android Studio (not in VSC), you can copy and paste from here, \\ will be
    // automatically transformed into \
    // standard asciidoc section
    // https://docs.asciidoctor.org/asciidoc/latest/sections/titles-and-levels/
    public static final Pattern PREFIX_ATX_HEADING = Pattern.compile("^( {0})(={1,6} {1})");
    //not yet adapted for asciidoc
    public static final Pattern PREFIX_CHECKBOX_LIST = Pattern.compile(
            "^( *)((\\*{1,6}) \\[( |\\*|x|X)] {1,})");
    public static final Pattern PREFIX_CHECKED_LIST = Pattern.compile(
            "^( *)((\\*{1,6}) \\[(\\*|x|X)] {1,})");
    public static final Pattern PREFIX_UNCHECKED_LIST = Pattern.compile(
            "^( *)((\\*{1,6}) \\[( )] {1,})");
    public static final Pattern PREFIX_UNORDERED_LIST = Pattern.compile("^( *)((\\*{1,6}) {1,})");
    public static final Pattern PREFIX_ORDERED_LIST = Pattern.compile("^( *)((\\.{1,6}) {1,})");
    //required as replacablePattern \s - any whitespace character: [\r\n\t\f\v ]
    public static final Pattern PREFIX_LEADING_SPACE = Pattern.compile("^( *)");
    //    TODO: to be removed
    // public static final Pattern PREFIX_QUOTE = Pattern.compile("^(>\\s)");
    // public static final Pattern PREFIX_LEADING_SPACE = Pattern.compile("^(\\s*)");

    //    public static final Pattern BLOCK_DELIMITER_COMMENT = Pattern.compile("^////");

    //    TODO: verstehen, wofür das gebraucht wird
    public static final AutoTextFormatter.FormatPatterns formatPatterns =
            new AutoTextFormatter.FormatPatterns(
                    AsciidocReplacePatternGenerator.PREFIX_UNORDERED_LIST,
                    AsciidocReplacePatternGenerator.PREFIX_CHECKBOX_LIST,
                    AsciidocReplacePatternGenerator.PREFIX_ORDERED_LIST, 2);
    // these patterns will be replaced, when we toggle Header, ordered or unordered list, checkbox
    public static final Pattern[] PREFIX_PATTERNS = {
            PREFIX_ORDERED_LIST,
            PREFIX_ATX_HEADING,
            PREFIX_CHECKED_LIST,
            PREFIX_UNCHECKED_LIST,
            // Unordered has to be after checked list. Otherwise checklist will match as an
            // unordered list.
            PREFIX_UNORDERED_LIST,
            PREFIX_LEADING_SPACE,
    };

    private final static String ORDERED_LIST_REPLACEMENT = "$11. ";

    /**
     * Set/unset ATX heading level on each selected line
     * <p>
     * This routine will make the following conditional changes
     * <p>
     * Line is heading of same level as requested -> remove heading
     * Line is heading of different level that that requested -> add heading of specified level
     * Line is not heading -> add heading of specified level
     *
     * @param level ATX heading level
     */
    public static List<ActionButtonBase.ReplacePattern> setOrUnsetHeadingWithLevel(int level) {

        // Create a new list in which patterns are inserted, which are then processed in order -
        // Replacements are performed.
        List<ActionButtonBase.ReplacePattern> patterns = new ArrayList<>();

        // AsciiDoc uses '=' to mark sections (headers).
        String heading = TextViewUtils.repeatChars('=', level);

        // pattern no 1:
        // Then and only then, if the current line matches the level, the header should be removed
        // Replace this exact heading level with nothing
        // Hint: RegExp.$1-$9 https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp/n
        //"$1" normaly is "", because it is the first group like "( {0})" or "( *)"
//        patterns.add(new ActionButtonBase.ReplacePattern("^" + heading + " ", "$1"));
        patterns.add(new ActionButtonBase.ReplacePattern("^" + heading + " ", ""));

        // pattern no 2:
        // Replace other headings
        // should be the same result as with replacePattern: heading + " "
//        patterns.add(new ActionButtonBase.ReplacePattern(
//                AsciidocReplacePatternGenerator.PREFIX_ATX_HEADING, "$1" + heading + " "));
        patterns.add(new ActionButtonBase.ReplacePattern(
                AsciidocReplacePatternGenerator.PREFIX_ATX_HEADING, heading + " "));

        // pattern no 3 to 8:
        // Replace all other prefixes with heading
        // this list PREFIX_PATTERNS contains everything which should now be replaced by heading
        // + "$1 ", which could be " ", "  ", "   " and so on, if the list character doesn't start at first column
        // But why 'heading + "$1 "' and not 'heading + " $1"'?

        for (final Pattern pp : AsciidocReplacePatternGenerator.PREFIX_PATTERNS) {
//            patterns.add(new ActionButtonBase.ReplacePattern(pp, heading + "$1 "));
            patterns.add(new ActionButtonBase.ReplacePattern(pp, heading + " "));
        }

        /*
example: for level = 1

. existing entries with prefix "= " should remove this prefix: +
  "= aaa" => "aaa"
. all other header lines are replaced by "$1= ": +
  "=== aaa" => "= "
. all matchings from PREFIX_PATTERNS should be replaced

* After the patterns are created, they are passed to the function `runRegexReplaceAction`:
         */

        return patterns;
    }

    // TODO: works correctly only for the first level, lower levels are removed but downgraded
    public static List<ActionButtonBase.ReplacePattern> toggleToCheckedOrUncheckedListPrefix(
            String listChar) {
        final String unchecked = "$1" + listChar + " [ ] ";
        final String checked = "$1" + listChar + " [x] ";
        return ReplacePatternGeneratorHelper.replaceWithTargetPatternOrAlternative(PREFIX_PATTERNS,
                PREFIX_UNCHECKED_LIST, unchecked, checked);
    }

    // TODO: works correctly only for the first level, lower levels are removed but downgraded on
    //  new insert
    public static List<ActionButtonBase.ReplacePattern> replaceWithUnorderedListPrefixOrRemovePrefix(
            String listChar) {
        final String unorderedListReplacement = "$1" + listChar + " ";
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(PREFIX_PATTERNS,
                PREFIX_UNORDERED_LIST, unorderedListReplacement);
    }

    // TODO: works correctly only for the first level, lower levels are removed but downgraded on
    //  new insert
    public static List<ActionButtonBase.ReplacePattern> replaceWithOrderedListPrefixOrRemovePrefix(
            String listChar) {
        final String orderedListReplacement = "$1" + listChar + " ";
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(PREFIX_PATTERNS,
                PREFIX_ORDERED_LIST, orderedListReplacement);
    }

}
