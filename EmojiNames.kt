package com.example.scikeyboard

/**
 * ইমোজি সার্চের জন্য নাম ও কীওয়ার্ড (ইংরেজি + কিছু বাংলিশ, যেমন "hasi", "kanna", "bhalobasha")।
 * প্রতিটি লাইন: ইমোজি|কীওয়ার্ড
 */
object EmojiNames {
    class Entry(val emoji: String, val words: String)

    private val P0: String =
        "#\uFE0F\u20E3|keycap # hash\n" +
        "*\uFE0F\u20E3|keycap * asterisk\n" +
        "0\uFE0F\u20E3|keycap 0 zero\n" +
        "1\uFE0F\u20E3|keycap 1 one\n" +
        "2\uFE0F\u20E3|keycap 2 two\n" +
        "3\uFE0F\u20E3|keycap 3 three\n" +
        "4\uFE0F\u20E3|keycap 4 four\n" +
        "5\uFE0F\u20E3|keycap 5 five\n" +
        "6\uFE0F\u20E3|keycap 6 six\n" +
        "7\uFE0F\u20E3|keycap 7 seven\n" +
        "8\uFE0F\u20E3|keycap 8 eight\n" +
        "9\uFE0F\u20E3|keycap 9 nine\n" +
        "\u00A9\uFE0F|copyright\n" +
        "\u00AE\uFE0F|registered\n" +
        "\u203C\uFE0F|double exclamation mark bangbang\n" +
        "\u2049\uFE0F|exclamation question mark interrobang\n" +
        "\u2122\uFE0F|trade mark tm\n" +
        "\u2139\uFE0F|information source\n" +
        "\u2194\uFE0F|left right arrow\n" +
        "\u2195\uFE0F|up down arrow\n" +
        "\u2196\uFE0F|up left arrow upper\n" +
        "\u2197\uFE0F|up right arrow upper\n" +
        "\u2198\uFE0F|down right arrow lower\n" +
        "\u2199\uFE0F|down left arrow lower\n" +
        "\u21A9\uFE0F|right arrow curving left with leftwards hook\n" +
        "\u21AA\uFE0F|left arrow curving right hook\n" +
        "\u231A|watch\n" +
        "\u231B|hourglass done\n" +
        "\u2328\uFE0F|keyboard\n" +
        "\u23CF\uFE0F|eject button\n" +
        "\u23E9|fast forward button\n" +
        "\u23EA|fast reverse button rewind\n" +
        "\u23EB|fast up button double arrow\n" +
        "\u23EC|fast down button double arrow\n" +
        "\u23ED\uFE0F|next track button\n" +
        "\u23EE\uFE0F|last track button previous\n" +
        "\u23EF\uFE0F|play or pause button\n" +
        "\u23F0|alarm clock\n" +
        "\u23F1\uFE0F|stopwatch\n" +
        "\u23F2\uFE0F|timer clock\n" +
        "\u23F3|hourglass not done sand flowing\n" +
        "\u23F8\uFE0F|pause button\n" +
        "\u23F9\uFE0F|stop button\n" +
        "\u23FA\uFE0F|record button\n" +
        "\u24C2\uFE0F|circled m\n" +
        "\u25AA\uFE0F|black small square\n" +
        "\u25AB\uFE0F|white small square\n" +
        "\u25B6\uFE0F|play button forward arrow\n" +
        "\u25C0\uFE0F|reverse button backward arrow\n" +
        "\u25FB\uFE0F|white medium square\n" +
        "\u25FC\uFE0F|black medium square\n" +
        "\u25FD|white medium small square\n" +
        "\u25FE|black medium small square\n" +
        "\u2600\uFE0F|sun sunny rod roddur surjo\n" +
        "\u2601\uFE0F|cloud\n" +
        "\u2602\uFE0F|umbrella open\n" +
        "\u2603\uFE0F|snowman snow with\n" +
        "\u2604\uFE0F|comet\n" +
        "\u260E\uFE0F|telephone phone\n" +
        "\u2611\uFE0F|check box with check ballot\n" +
        "\u2614|umbrella with rain drops\n" +
        "\u2615|hot beverage coffee cha\n" +
        "\u2618\uFE0F|shamrock\n" +
        "\u261D\uFE0F|index pointing up point\n" +
        "\u2620\uFE0F|skull and crossbones\n" +
        "\u2622\uFE0F|radioactive\n" +
        "\u2623\uFE0F|biohazard\n" +
        "\u2626\uFE0F|orthodox cross\n" +
        "\u262A\uFE0F|star and crescent\n" +
        "\u262E\uFE0F|peace symbol\n" +
        "\u262F\uFE0F|yin yang\n" +
        "\u2638\uFE0F|wheel of dharma\n" +
        "\u2639\uFE0F|frowning face\n" +
        "\u263A\uFE0F|smiling face relaxed\n" +
        "\u2640\uFE0F|female sign\n" +
        "\u2642\uFE0F|male sign\n" +
        "\u2648|aries\n" +
        "\u2649|taurus\n" +
        "\u264A|gemini\n" +
        "\u264B|cancer\n" +
        "\u264C|leo\n" +
        "\u264D|virgo\n" +
        "\u264E|libra\n" +
        "\u264F|scorpio scorpius\n" +
        "\u2650|sagittarius\n" +
        "\u2651|capricorn\n" +
        "\u2652|aquarius\n" +
        "\u2653|pisces\n" +
        "\u265F\uFE0F|chess pawn\n" +
        "\u2660\uFE0F|spade suit spades\n" +
        "\u2663\uFE0F|club suit clubs\n" +
        "\u2665\uFE0F|heart suit hearts\n" +
        "\u2666\uFE0F|diamond suit diamonds\n" +
        "\u2668\uFE0F|hot springs hotsprings\n" +
        "\u267B\uFE0F|recycling symbol recycle\n" +
        "\u267E\uFE0F|infinity\n" +
        "\u267F|wheelchair symbol\n" +
        "\u2692\uFE0F|hammer and pick\n" +
        "\u2693|anchor\n" +
        "\u2694\uFE0F|crossed swords\n" +
        "\u2695\uFE0F|medical symbol\n" +
        "\u2696\uFE0F|balance scale\n" +
        "\u2697\uFE0F|alembic\n" +
        "\u2699\uFE0F|gear\n" +
        "\u269B\uFE0F|atom symbol\n" +
        "\u269C\uFE0F|fleur de lis\n" +
        "\u26A0\uFE0F|warning\n" +
        "\u26A1|high voltage zap\n" +
        "\u26A7\uFE0F|transgender symbol\n" +
        "\u26AA|white circle\n" +
        "\u26AB|black circle\n" +
        "\u26B0\uFE0F|coffin\n" +
        "\u26B1\uFE0F|funeral urn\n" +
        "\u26BD|soccer ball\n" +
        "\u26BE|baseball\n" +
        "\u26C4|snowman without snow\n" +
        "\u26C5|sun behind cloud sunny partly\n" +
        "\u26C8\uFE0F|cloud with lightning and rain\n" +
        "\u26CE|ophiuchus\n" +
        "\u26CF\uFE0F|pick\n" +
        "\u26D1\uFE0F|rescue worker\u2019s helmet worker\n" +
        "\u26D3\uFE0F|chains\n" +
        "\u26D4|no entry\n" +
        "\u26E9\uFE0F|shinto shrine\n" +
        "\u26EA|church\n" +
        "\u26F0\uFE0F|mountain\n" +
        "\u26F1\uFE0F|umbrella on ground parasol\n" +
        "\u26F2|fountain\n" +
        "\u26F3|flag in hole golf\n" +
        "\u26F4\uFE0F|ferry\n" +
        "\u26F5|sailboat boat\n" +
        "\u26F7\uFE0F|skier\n" +
        "\u26F8\uFE0F|ice skate\n" +
        "\u26F9\uFE0F|person bouncing ball\n" +
        "\u26F9\uFE0F\u200D\u2642\uFE0F|man bouncing ball\n" +
        "\u26F9\uFE0F\u200D\u2640\uFE0F|woman bouncing ball\n" +
        "\u26FA|tent\n" +
        "\u26FD|fuel pump fuelpump\n" +
        "\u2702\uFE0F|scissors\n" +
        "\u2705|check mark button white\n" +
        "\u2708\uFE0F|airplane\n" +
        "\u2709\uFE0F|envelope\n" +
        "\u270A|raised fist\n" +
        "\u270B|raised hand\n" +
        "\u270C\uFE0F|victory hand v\n" +
        "\u270D\uFE0F|writing hand\n" +
        "\u270F\uFE0F|pencil pencil2\n" +
        "\u2712\uFE0F|black nib\n" +
        "\u2714\uFE0F|check mark heavy\n" +
        "\u2716\uFE0F|multiply x heavy multiplication\n" +
        "\u271D\uFE0F|latin cross\n" +
        "\u2721\uFE0F|star of david\n" +
        "\u2728|sparkles\n" +
        "\u2733\uFE0F|eight spoked asterisk\n" +
        "\u2734\uFE0F|eight pointed star black\n" +
        "\u2744\uFE0F|snowflake\n" +
        "\u2747\uFE0F|sparkle\n" +
        "\u274C|cross mark x\n" +
        "\u274E|cross mark button negative squared\n" +
        "\u2753|red question mark\n" +
        "\u2754|white question mark grey\n" +
        "\u2755|white exclamation mark grey\n" +
        "\u2757|red exclamation mark\n" +
        "\u2763\uFE0F|heart exclamation heavy\n" +
        "\u2764\uFE0F|red heart bhalobasha prem pyar dil hridoy love\n" +
        "\u2764\uFE0F\u200D\uD83D\uDD25|heart on fire\n" +
        "\u2764\uFE0F\u200D\uD83E\uDE79|mending heart\n" +
        "\u2795|plus heavy sign\n" +
        "\u2796|minus sign heavy\n" +
        "\u2797|divide sign heavy division\n" +
        "\u27A1\uFE0F|right arrow\n" +
        "\u27B0|curly loop\n" +
        "\u27BF|double curly loop\n" +
        "\u2934\uFE0F|right arrow curving up heading\n" +
        "\u2935\uFE0F|right arrow curving down heading\n" +
        "\u2B05\uFE0F|left arrow\n" +
        "\u2B06\uFE0F|up arrow\n" +
        "\u2B07\uFE0F|down arrow\n" +
        "\u2B1B|black large square\n" +
        "\u2B1C|white large square\n" +
        "\u2B50|star\n" +
        "\u2B55|hollow red circle o\n" +
        "\u3030\uFE0F|wavy dash\n" +
        "\u303D\uFE0F|part alternation mark\n" +
        "\u3297\uFE0F|japanese \u201Ccongratulations\u201D button congratulations\n" +
        "\u3299\uFE0F|japanese \u201Csecret\u201D button secret\n" +
        "\uD83C\uDC04|mahjong red dragon\n" +
        "\uD83C\uDCCF|joker black\n" +
        "\uD83C\uDD70\uFE0F|a button (blood type)\n" +
        "\uD83C\uDD71\uFE0F|b button (blood type)\n" +
        "\uD83C\uDD7E\uFE0F|o button (blood type) o2\n" +
        "\uD83C\uDD7F\uFE0F|p button parking\n" +
        "\uD83C\uDD8E|ab button (blood type)\n" +
        "\uD83C\uDD91|cl button\n" +
        "\uD83C\uDD92|cool button\n" +
        "\uD83C\uDD93|free button\n" +
        "\uD83C\uDD94|id button\n" +
        "\uD83C\uDD95|new button\n" +
        "\uD83C\uDD96|ng button\n" +
        "\uD83C\uDD97|ok button\n" +
        "\uD83C\uDD98|sos button\n" +
        "\uD83C\uDD99|up! button up\n" +
        "\uD83C\uDD9A|vs button\n" +
        "\uD83C\uDDE6\uD83C\uDDEB|flag afghanistan\n" +
        "\uD83C\uDDE6\uD83C\uDDFD|flag \u00E5land islands aland\n" +
        "\uD83C\uDDE6\uD83C\uDDF1|flag albania\n" +
        "\uD83C\uDDE6\uD83C\uDDF8|flag american samoa\n" +
        "\uD83C\uDDE6\uD83C\uDDE9|flag andorra\n" +
        "\uD83C\uDDE6\uD83C\uDDF4|flag angola\n" +
        "\uD83C\uDDE6\uD83C\uDDEE|flag anguilla\n" +
        "\uD83C\uDDE6\uD83C\uDDF6|flag antarctica\n" +
        "\uD83C\uDDE6\uD83C\uDDEC|flag antigua & barbuda\n" +
        "\uD83C\uDDE6\uD83C\uDDF7|flag argentina\n" +
        "\uD83C\uDDE6\uD83C\uDDF2|flag armenia\n" +
        "\uD83C\uDDE6\uD83C\uDDFC|flag aruba\n" +
        "\uD83C\uDDE6\uD83C\uDDE8|flag ascension island\n" +
        "\uD83C\uDDE6\uD83C\uDDFA|flag australia\n" +
        "\uD83C\uDDE6\uD83C\uDDF9|flag austria\n" +
        "\uD83C\uDDE6\uD83C\uDDFF|flag azerbaijan\n" +
        "\uD83C\uDDE6\uD83C\uDDEA|flag united arab emirates\n" +
        "\uD83C\uDDE7\uD83C\uDDF8|flag bahamas\n" +
        "\uD83C\uDDE7\uD83C\uDDED|flag bahrain\n" +
        "\uD83C\uDDE7\uD83C\uDDE9|flag bangladesh bangla desh\n" +
        "\uD83C\uDDE7\uD83C\uDDE7|flag barbados\n" +
        "\uD83C\uDDE7\uD83C\uDDFE|flag belarus\n" +
        "\uD83C\uDDE7\uD83C\uDDEA|flag belgium\n" +
        "\uD83C\uDDE7\uD83C\uDDFF|flag belize\n" +
        "\uD83C\uDDE7\uD83C\uDDEF|flag benin\n" +
        "\uD83C\uDDE7\uD83C\uDDF2|flag bermuda\n" +
        "\uD83C\uDDE7\uD83C\uDDF9|flag bhutan\n" +
        "\uD83C\uDDE7\uD83C\uDDF4|flag bolivia\n" +
        "\uD83C\uDDE7\uD83C\uDDE6|flag bosnia & herzegovina\n" +
        "\uD83C\uDDE7\uD83C\uDDFC|flag botswana\n" +
        "\uD83C\uDDE7\uD83C\uDDFB|flag bouvet island\n" +
        "\uD83C\uDDE7\uD83C\uDDF7|flag brazil\n" +
        "\uD83C\uDDE7\uD83C\uDDF3|flag brunei\n" +
        "\uD83C\uDDE7\uD83C\uDDEC|flag bulgaria\n" +
        "\uD83C\uDDE7\uD83C\uDDEB|flag burkina faso\n" +
        "\uD83C\uDDE7\uD83C\uDDEE|flag burundi\n" +
        "\uD83C\uDDE7\uD83C\uDDF6|flag caribbean netherlands\n" +
        "\uD83C\uDDE7\uD83C\uDDF1|flag st. barth\u00E9lemy barthelemy st\n" +
        "\uD83C\uDDE8\uD83C\uDDF2|flag cameroon\n" +
        "\uD83C\uDDE8\uD83C\uDDE6|flag canada\n" +
        "\uD83C\uDDE8\uD83C\uDDFB|flag cape verde\n" +
        "\uD83C\uDDE8\uD83C\uDDEB|flag central african republic\n" +
        "\uD83C\uDDE8\uD83C\uDDF1|flag chile\n" +
        "\uD83C\uDDE8\uD83C\uDDFD|flag christmas island\n" +
        "\uD83C\uDDE8\uD83C\uDDF5|flag clipperton island\n" +
        "\uD83C\uDDE8\uD83C\uDDF3|flag china cn\n" +
        "\uD83C\uDDE8\uD83C\uDDE8|flag cocos (keeling) islands\n" +
        "\uD83C\uDDE8\uD83C\uDDF4|flag colombia\n" +
        "\uD83C\uDDE8\uD83C\uDDEC|flag congo brazzaville\n" +
        "\uD83C\uDDE8\uD83C\uDDE9|flag congo kinshasa\n" +
        "\uD83C\uDDE8\uD83C\uDDF0|flag cook islands\n" +
        "\uD83C\uDDE8\uD83C\uDDF7|flag costa rica\n" +
        "\uD83C\uDDE8\uD83C\uDDEE|flag c\u00F4te d\u2019ivoire divoire cote\n" +
        "\uD83C\uDDE8\uD83C\uDDFA|flag cuba\n" +
        "\uD83C\uDDE8\uD83C\uDDFC|flag cura\u00E7ao curacao\n" +
        "\uD83C\uDDE8\uD83C\uDDFE|flag cyprus\n" +
        "\uD83C\uDDE8\uD83C\uDDFF|flag czechia czech republic\n" +
        "\uD83C\uDDE8\uD83C\uDDED|flag switzerland\n" +
        "\uD83C\uDDE9\uD83C\uDDFF|flag algeria\n" +
        "\uD83C\uDDE9\uD83C\uDDEA|flag germany de\n" +
        "\uD83C\uDDE9\uD83C\uDDF0|flag denmark\n" +
        "\uD83C\uDDE9\uD83C\uDDEC|flag diego garcia\n" +
        "\uD83C\uDDE9\uD83C\uDDEF|flag djibouti\n" +
        "\uD83C\uDDE9\uD83C\uDDF2|flag dominica\n" +
        "\uD83C\uDDE9\uD83C\uDDF4|flag dominican republic\n" +
        "\uD83C\uDDEA\uD83C\uDDE6|flag ceuta & melilla\n" +
        "\uD83C\uDDEA\uD83C\uDDE8|flag ecuador\n" +
        "\uD83C\uDDEA\uD83C\uDDEC|flag egypt\n" +
        "\uD83C\uDDEA\uD83C\uDDF7|flag eritrea\n" +
        "\uD83C\uDDEA\uD83C\uDDF8|flag spain es\n" +
        "\uD83C\uDDEA\uD83C\uDDEA|flag estonia\n"

    private val P1: String =
        "\uD83C\uDDEA\uD83C\uDDF9|flag ethiopia\n" +
        "\uD83C\uDDEA\uD83C\uDDFA|flag european union eu\n" +
        "\uD83C\uDDEA\uD83C\uDDED|flag western sahara\n" +
        "\uD83C\uDDEB\uD83C\uDDF0|flag falkland islands\n" +
        "\uD83C\uDDEB\uD83C\uDDF4|flag faroe islands\n" +
        "\uD83C\uDDEB\uD83C\uDDEF|flag fiji\n" +
        "\uD83C\uDDEB\uD83C\uDDEE|flag finland\n" +
        "\uD83C\uDDEB\uD83C\uDDF7|flag france fr\n" +
        "\uD83C\uDDEB\uD83C\uDDF2|flag micronesia\n" +
        "\uD83C\uDDEC\uD83C\uDDF6|flag equatorial guinea\n" +
        "\uD83C\uDDEC\uD83C\uDDEB|flag french guiana\n" +
        "\uD83C\uDDEC\uD83C\uDDE6|flag gabon\n" +
        "\uD83C\uDDEC\uD83C\uDDF2|flag gambia\n" +
        "\uD83C\uDDEC\uD83C\uDDE7|flag united kingdom gb\n" +
        "\uD83C\uDDEC\uD83C\uDDEA|flag georgia\n" +
        "\uD83C\uDDEC\uD83C\uDDED|flag ghana\n" +
        "\uD83C\uDDEC\uD83C\uDDEE|flag gibraltar\n" +
        "\uD83C\uDDEC\uD83C\uDDF7|flag greece\n" +
        "\uD83C\uDDEC\uD83C\uDDF1|flag greenland\n" +
        "\uD83C\uDDEC\uD83C\uDDE9|flag grenada\n" +
        "\uD83C\uDDEC\uD83C\uDDF5|flag guadeloupe\n" +
        "\uD83C\uDDEC\uD83C\uDDFA|flag guam\n" +
        "\uD83C\uDDEC\uD83C\uDDF9|flag guatemala\n" +
        "\uD83C\uDDEC\uD83C\uDDEC|flag guernsey\n" +
        "\uD83C\uDDEC\uD83C\uDDF3|flag guinea\n" +
        "\uD83C\uDDEC\uD83C\uDDFC|flag guinea bissau\n" +
        "\uD83C\uDDEC\uD83C\uDDFE|flag guyana\n" +
        "\uD83C\uDDEC\uD83C\uDDF8|flag south georgia & south sandwich islands\n" +
        "\uD83C\uDDED\uD83C\uDDF7|flag croatia\n" +
        "\uD83C\uDDED\uD83C\uDDF9|flag haiti\n" +
        "\uD83C\uDDED\uD83C\uDDF2|flag heard & mcdonald islands\n" +
        "\uD83C\uDDED\uD83C\uDDF3|flag honduras\n" +
        "\uD83C\uDDED\uD83C\uDDF0|flag hong kong sar china\n" +
        "\uD83C\uDDED\uD83C\uDDFA|flag hungary\n" +
        "\uD83C\uDDEE\uD83C\uDDF4|flag british indian ocean territory\n" +
        "\uD83C\uDDEE\uD83C\uDDE8|flag canary islands\n" +
        "\uD83C\uDDEE\uD83C\uDDF8|flag iceland\n" +
        "\uD83C\uDDEE\uD83C\uDDF3|flag india\n" +
        "\uD83C\uDDEE\uD83C\uDDE9|flag indonesia\n" +
        "\uD83C\uDDEE\uD83C\uDDF7|flag iran\n" +
        "\uD83C\uDDEE\uD83C\uDDF6|flag iraq\n" +
        "\uD83C\uDDEE\uD83C\uDDEA|flag ireland\n" +
        "\uD83C\uDDEE\uD83C\uDDF2|flag isle of man\n" +
        "\uD83C\uDDEE\uD83C\uDDF1|flag israel\n" +
        "\uD83C\uDDEE\uD83C\uDDF9|flag italy it\n" +
        "\uD83C\uDDEF\uD83C\uDDF2|flag jamaica\n" +
        "\uD83C\uDDEF\uD83C\uDDEA|flag jersey\n" +
        "\uD83C\uDDEF\uD83C\uDDF4|flag jordan\n" +
        "\uD83C\uDDEF\uD83C\uDDF5|flag japan jp\n" +
        "\uD83C\uDDF0\uD83C\uDDED|flag cambodia\n" +
        "\uD83C\uDDF0\uD83C\uDDFE|flag cayman islands\n" +
        "\uD83C\uDDF0\uD83C\uDDF2|flag comoros\n" +
        "\uD83C\uDDF0\uD83C\uDDFF|flag kazakhstan\n" +
        "\uD83C\uDDF0\uD83C\uDDEA|flag kenya\n" +
        "\uD83C\uDDF0\uD83C\uDDEE|flag kiribati\n" +
        "\uD83C\uDDF0\uD83C\uDDF7|flag south korea kr\n" +
        "\uD83C\uDDF0\uD83C\uDDFC|flag kuwait\n" +
        "\uD83C\uDDF0\uD83C\uDDEC|flag kyrgyzstan\n" +
        "\uD83C\uDDF0\uD83C\uDDF5|flag north korea\n" +
        "\uD83C\uDDF0\uD83C\uDDF3|flag st. kitts & nevis st\n" +
        "\uD83C\uDDF1\uD83C\uDDE6|flag laos\n" +
        "\uD83C\uDDF1\uD83C\uDDFB|flag latvia\n" +
        "\uD83C\uDDF1\uD83C\uDDE7|flag lebanon\n" +
        "\uD83C\uDDF1\uD83C\uDDF8|flag lesotho\n" +
        "\uD83C\uDDF1\uD83C\uDDF7|flag liberia\n" +
        "\uD83C\uDDF1\uD83C\uDDFE|flag libya\n" +
        "\uD83C\uDDF1\uD83C\uDDEE|flag liechtenstein\n" +
        "\uD83C\uDDF1\uD83C\uDDF9|flag lithuania\n" +
        "\uD83C\uDDF1\uD83C\uDDFA|flag luxembourg\n" +
        "\uD83C\uDDF1\uD83C\uDDF0|flag sri lanka\n" +
        "\uD83C\uDDF1\uD83C\uDDE8|flag st. lucia st\n" +
        "\uD83C\uDDF2\uD83C\uDDF4|flag macao sar china macau\n" +
        "\uD83C\uDDF2\uD83C\uDDF0|flag north macedonia\n" +
        "\uD83C\uDDF2\uD83C\uDDEC|flag madagascar\n" +
        "\uD83C\uDDF2\uD83C\uDDFC|flag malawi\n" +
        "\uD83C\uDDF2\uD83C\uDDFE|flag malaysia\n" +
        "\uD83C\uDDF2\uD83C\uDDFB|flag maldives\n" +
        "\uD83C\uDDF2\uD83C\uDDF1|flag mali\n" +
        "\uD83C\uDDF2\uD83C\uDDF9|flag malta\n" +
        "\uD83C\uDDF2\uD83C\uDDED|flag marshall islands\n" +
        "\uD83C\uDDF2\uD83C\uDDF6|flag martinique\n" +
        "\uD83C\uDDF2\uD83C\uDDF7|flag mauritania\n" +
        "\uD83C\uDDF2\uD83C\uDDFA|flag mauritius\n" +
        "\uD83C\uDDF2\uD83C\uDDFD|flag mexico\n" +
        "\uD83C\uDDF2\uD83C\uDDE9|flag moldova\n" +
        "\uD83C\uDDF2\uD83C\uDDE8|flag monaco\n" +
        "\uD83C\uDDF2\uD83C\uDDF3|flag mongolia\n" +
        "\uD83C\uDDF2\uD83C\uDDEA|flag montenegro\n" +
        "\uD83C\uDDF2\uD83C\uDDF8|flag montserrat\n" +
        "\uD83C\uDDF2\uD83C\uDDE6|flag morocco\n" +
        "\uD83C\uDDF2\uD83C\uDDFF|flag mozambique\n" +
        "\uD83C\uDDF2\uD83C\uDDF2|flag myanmar (burma)\n" +
        "\uD83C\uDDF2\uD83C\uDDF5|flag northern mariana islands\n" +
        "\uD83C\uDDF2\uD83C\uDDEB|flag st. martin st\n" +
        "\uD83C\uDDF3\uD83C\uDDE6|flag namibia\n" +
        "\uD83C\uDDF3\uD83C\uDDF7|flag nauru\n" +
        "\uD83C\uDDF3\uD83C\uDDF5|flag nepal\n" +
        "\uD83C\uDDF3\uD83C\uDDF1|flag netherlands\n" +
        "\uD83C\uDDF3\uD83C\uDDE8|flag new caledonia\n" +
        "\uD83C\uDDF3\uD83C\uDDFF|flag new zealand\n" +
        "\uD83C\uDDF3\uD83C\uDDEE|flag nicaragua\n" +
        "\uD83C\uDDF3\uD83C\uDDEA|flag niger\n" +
        "\uD83C\uDDF3\uD83C\uDDEC|flag nigeria\n" +
        "\uD83C\uDDF3\uD83C\uDDFA|flag niue\n" +
        "\uD83C\uDDF3\uD83C\uDDEB|flag norfolk island\n" +
        "\uD83C\uDDF3\uD83C\uDDF4|flag norway\n" +
        "\uD83C\uDDF4\uD83C\uDDF2|flag oman\n" +
        "\uD83C\uDDF5\uD83C\uDDEB|flag french polynesia\n" +
        "\uD83C\uDDF5\uD83C\uDDF0|flag pakistan\n" +
        "\uD83C\uDDF5\uD83C\uDDFC|flag palau\n" +
        "\uD83C\uDDF5\uD83C\uDDF8|flag palestinian territories\n" +
        "\uD83C\uDDF5\uD83C\uDDE6|flag panama\n" +
        "\uD83C\uDDF5\uD83C\uDDEC|flag papua new guinea\n" +
        "\uD83C\uDDF5\uD83C\uDDFE|flag paraguay\n" +
        "\uD83C\uDDF5\uD83C\uDDEA|flag peru\n" +
        "\uD83C\uDDF5\uD83C\uDDED|flag philippines\n" +
        "\uD83C\uDDF5\uD83C\uDDF3|flag pitcairn islands\n" +
        "\uD83C\uDDF5\uD83C\uDDF1|flag poland\n" +
        "\uD83C\uDDF5\uD83C\uDDF9|flag portugal\n" +
        "\uD83C\uDDF5\uD83C\uDDF7|flag puerto rico\n" +
        "\uD83C\uDDF5\uD83C\uDDF2|flag st. pierre & miquelon st\n" +
        "\uD83C\uDDF6\uD83C\uDDE6|flag qatar\n" +
        "\uD83C\uDDF7\uD83C\uDDEA|flag r\u00E9union reunion\n" +
        "\uD83C\uDDF7\uD83C\uDDF4|flag romania\n" +
        "\uD83C\uDDF7\uD83C\uDDFA|flag russia ru\n" +
        "\uD83C\uDDF7\uD83C\uDDFC|flag rwanda\n" +
        "\uD83C\uDDF7\uD83C\uDDF8|flag serbia\n" +
        "\uD83C\uDDF8\uD83C\uDDFB|flag el salvador\n" +
        "\uD83C\uDDF8\uD83C\uDDF2|flag san marino\n" +
        "\uD83C\uDDF8\uD83C\uDDF9|flag s\u00E3o tom\u00E9 & pr\u00EDncipe tome principe sao\n" +
        "\uD83C\uDDF8\uD83C\uDDE6|flag saudi arabia\n" +
        "\uD83C\uDDF8\uD83C\uDDF3|flag senegal\n" +
        "\uD83C\uDDF8\uD83C\uDDE8|flag seychelles\n" +
        "\uD83C\uDDF8\uD83C\uDDF1|flag sierra leone\n" +
        "\uD83C\uDDF8\uD83C\uDDEC|flag singapore\n" +
        "\uD83C\uDDF8\uD83C\uDDFD|flag sint maarten\n" +
        "\uD83C\uDDF8\uD83C\uDDF0|flag slovakia\n" +
        "\uD83C\uDDF8\uD83C\uDDEE|flag slovenia\n" +
        "\uD83C\uDDF8\uD83C\uDDE7|flag solomon islands\n" +
        "\uD83C\uDDF8\uD83C\uDDF4|flag somalia\n" +
        "\uD83C\uDDF8\uD83C\uDDF8|flag south sudan\n" +
        "\uD83C\uDDF8\uD83C\uDDED|flag st. helena st\n" +
        "\uD83C\uDDF8\uD83C\uDDE9|flag sudan\n" +
        "\uD83C\uDDF8\uD83C\uDDF7|flag suriname\n" +
        "\uD83C\uDDF8\uD83C\uDDEF|flag svalbard & jan mayen\n" +
        "\uD83C\uDDF8\uD83C\uDDFF|flag eswatini swaziland\n" +
        "\uD83C\uDDF8\uD83C\uDDEA|flag sweden\n" +
        "\uD83C\uDDF8\uD83C\uDDFE|flag syria\n" +
        "\uD83C\uDDF9\uD83C\uDDE9|flag chad\n" +
        "\uD83C\uDDF9\uD83C\uDDEB|flag french southern territories\n" +
        "\uD83C\uDDF9\uD83C\uDDFC|flag taiwan\n" +
        "\uD83C\uDDF9\uD83C\uDDEF|flag tajikistan\n" +
        "\uD83C\uDDF9\uD83C\uDDFF|flag tanzania\n" +
        "\uD83C\uDDF9\uD83C\uDDED|flag thailand\n" +
        "\uD83C\uDDF9\uD83C\uDDF1|flag timor leste\n" +
        "\uD83C\uDDF9\uD83C\uDDEC|flag togo\n" +
        "\uD83C\uDDF9\uD83C\uDDF0|flag tokelau\n" +
        "\uD83C\uDDF9\uD83C\uDDF4|flag tonga\n" +
        "\uD83C\uDDF9\uD83C\uDDF7|flag turkey tr\n" +
        "\uD83C\uDDF9\uD83C\uDDF9|flag trinidad & tobago\n" +
        "\uD83C\uDDF9\uD83C\uDDE6|flag tristan da cunha\n" +
        "\uD83C\uDDF9\uD83C\uDDF3|flag tunisia\n" +
        "\uD83C\uDDF9\uD83C\uDDF2|flag turkmenistan\n" +
        "\uD83C\uDDF9\uD83C\uDDE8|flag turks & caicos islands\n" +
        "\uD83C\uDDF9\uD83C\uDDFB|flag tuvalu\n" +
        "\uD83C\uDDFA\uD83C\uDDEC|flag uganda\n" +
        "\uD83C\uDDFA\uD83C\uDDE6|flag ukraine\n" +
        "\uD83C\uDDFA\uD83C\uDDF3|flag united nations\n" +
        "\uD83C\uDDFA\uD83C\uDDFE|flag uruguay\n" +
        "\uD83C\uDDFA\uD83C\uDDF8|flag united states us\n" +
        "\uD83C\uDDFA\uD83C\uDDF2|flag u.s. outlying islands us\n" +
        "\uD83C\uDDFA\uD83C\uDDFF|flag uzbekistan\n" +
        "\uD83C\uDDFB\uD83C\uDDEC|flag british virgin islands\n" +
        "\uD83C\uDDFB\uD83C\uDDE8|flag st. vincent & grenadines st\n" +
        "\uD83C\uDDFB\uD83C\uDDEE|flag u.s. virgin islands us\n" +
        "\uD83C\uDDFB\uD83C\uDDFA|flag vanuatu\n" +
        "\uD83C\uDDFB\uD83C\uDDE6|flag vatican city\n" +
        "\uD83C\uDDFB\uD83C\uDDEA|flag venezuela\n" +
        "\uD83C\uDDFB\uD83C\uDDF3|flag vietnam\n" +
        "\uD83C\uDDFC\uD83C\uDDF8|flag samoa\n" +
        "\uD83C\uDDFC\uD83C\uDDEB|flag wallis & futuna\n" +
        "\uD83C\uDDFD\uD83C\uDDF0|flag kosovo\n" +
        "\uD83C\uDDFE\uD83C\uDDF9|flag mayotte\n" +
        "\uD83C\uDDFE\uD83C\uDDEA|flag yemen\n" +
        "\uD83C\uDDFF\uD83C\uDDE6|flag south africa\n" +
        "\uD83C\uDDFF\uD83C\uDDF2|flag zambia\n" +
        "\uD83C\uDDFF\uD83C\uDDFC|flag zimbabwe\n" +
        "\uD83C\uDE01|japanese \u201Chere\u201D button koko\n" +
        "\uD83C\uDE02\uFE0F|japanese \u201Cservice charge\u201D button sa\n" +
        "\uD83C\uDE1A|japanese \u201Cfree of charge\u201D button u7121\n" +
        "\uD83C\uDE2F|japanese \u201Creserved\u201D button u6307\n" +
        "\uD83C\uDE32|japanese \u201Cprohibited\u201D button u7981\n" +
        "\uD83C\uDE33|japanese \u201Cvacancy\u201D button u7a7a\n" +
        "\uD83C\uDE34|japanese \u201Cpassing grade\u201D button u5408\n" +
        "\uD83C\uDE35|japanese \u201Cno vacancy\u201D button u6e80\n" +
        "\uD83C\uDE36|japanese \u201Cnot free of charge\u201D button u6709\n" +
        "\uD83C\uDE37\uFE0F|japanese \u201Cmonthly amount\u201D button u6708\n" +
        "\uD83C\uDE38|japanese \u201Capplication\u201D button u7533\n" +
        "\uD83C\uDE39|japanese \u201Cdiscount\u201D button u5272\n" +
        "\uD83C\uDE3A|japanese \u201Copen for business\u201D button u55b6\n" +
        "\uD83C\uDE50|japanese \u201Cbargain\u201D button ideograph advantage\n" +
        "\uD83C\uDE51|japanese \u201Cacceptable\u201D button accept\n" +
        "\uD83C\uDF00|cyclone\n" +
        "\uD83C\uDF01|foggy\n" +
        "\uD83C\uDF02|closed umbrella\n" +
        "\uD83C\uDF03|night with stars\n" +
        "\uD83C\uDF04|sunrise over mountains\n" +
        "\uD83C\uDF05|sunrise\n"

    private val P2: String =
        "\uD83C\uDF06|cityscape at dusk city sunset\n" +
        "\uD83C\uDF07|sunset city sunrise\n" +
        "\uD83C\uDF08|rainbow\n" +
        "\uD83C\uDF09|bridge at night\n" +
        "\uD83C\uDF0A|water wave ocean\n" +
        "\uD83C\uDF0B|volcano\n" +
        "\uD83C\uDF0C|milky way\n" +
        "\uD83C\uDF0D|globe showing europe africa earth\n" +
        "\uD83C\uDF0E|globe showing americas earth\n" +
        "\uD83C\uDF0F|globe showing asia australia earth\n" +
        "\uD83C\uDF10|globe with meridians\n" +
        "\uD83C\uDF11|new moon\n" +
        "\uD83C\uDF12|waxing crescent moon\n" +
        "\uD83C\uDF13|first quarter moon\n" +
        "\uD83C\uDF14|waxing gibbous moon\n" +
        "\uD83C\uDF15|full moon\n" +
        "\uD83C\uDF16|waning gibbous moon\n" +
        "\uD83C\uDF17|last quarter moon\n" +
        "\uD83C\uDF18|waning crescent moon\n" +
        "\uD83C\uDF19|crescent moon chand chaad rat\n" +
        "\uD83C\uDF1A|new moon face with\n" +
        "\uD83C\uDF1B|first quarter moon face with\n" +
        "\uD83C\uDF1C|last quarter moon face with\n" +
        "\uD83C\uDF1D|full moon face with\n" +
        "\uD83C\uDF1E|sun with face\n" +
        "\uD83C\uDF1F|glowing star star2\n" +
        "\uD83C\uDF20|shooting star stars\n" +
        "\uD83C\uDF21\uFE0F|thermometer\n" +
        "\uD83C\uDF24\uFE0F|sun behind small cloud\n" +
        "\uD83C\uDF25\uFE0F|sun behind large cloud\n" +
        "\uD83C\uDF26\uFE0F|sun behind rain cloud\n" +
        "\uD83C\uDF27\uFE0F|cloud with rain brishti bristi\n" +
        "\uD83C\uDF28\uFE0F|cloud with snow\n" +
        "\uD83C\uDF29\uFE0F|cloud with lightning\n" +
        "\uD83C\uDF2A\uFE0F|tornado\n" +
        "\uD83C\uDF2B\uFE0F|fog\n" +
        "\uD83C\uDF2C\uFE0F|wind face\n" +
        "\uD83C\uDF2D|hot dog hotdog\n" +
        "\uD83C\uDF2E|taco\n" +
        "\uD83C\uDF2F|burrito\n" +
        "\uD83C\uDF30|chestnut\n" +
        "\uD83C\uDF31|seedling\n" +
        "\uD83C\uDF32|evergreen tree\n" +
        "\uD83C\uDF33|deciduous tree\n" +
        "\uD83C\uDF34|palm tree\n" +
        "\uD83C\uDF35|cactus\n" +
        "\uD83C\uDF36\uFE0F|hot pepper\n" +
        "\uD83C\uDF37|tulip\n" +
        "\uD83C\uDF38|cherry blossom\n" +
        "\uD83C\uDF39|rose\n" +
        "\uD83C\uDF3A|hibiscus\n" +
        "\uD83C\uDF3B|sunflower\n" +
        "\uD83C\uDF3C|blossom\n" +
        "\uD83C\uDF3D|ear of corn\n" +
        "\uD83C\uDF3E|sheaf of rice ear\n" +
        "\uD83C\uDF3F|herb\n" +
        "\uD83C\uDF40|four leaf clover\n" +
        "\uD83C\uDF41|maple leaf\n" +
        "\uD83C\uDF42|fallen leaf\n" +
        "\uD83C\uDF43|leaf fluttering in wind leaves\n" +
        "\uD83C\uDF44|mushroom\n" +
        "\uD83C\uDF45|tomato\n" +
        "\uD83C\uDF46|eggplant\n" +
        "\uD83C\uDF47|grapes\n" +
        "\uD83C\uDF48|melon\n" +
        "\uD83C\uDF49|watermelon\n" +
        "\uD83C\uDF4A|tangerine\n" +
        "\uD83C\uDF4B|lemon\n" +
        "\uD83C\uDF4C|banana\n" +
        "\uD83C\uDF4D|pineapple\n" +
        "\uD83C\uDF4E|red apple\n" +
        "\uD83C\uDF4F|green apple\n" +
        "\uD83C\uDF50|pear\n" +
        "\uD83C\uDF51|peach\n" +
        "\uD83C\uDF52|cherries\n" +
        "\uD83C\uDF53|strawberry\n" +
        "\uD83C\uDF54|hamburger\n" +
        "\uD83C\uDF55|pizza khabar\n" +
        "\uD83C\uDF56|meat on bone\n" +
        "\uD83C\uDF57|poultry leg\n" +
        "\uD83C\uDF58|rice cracker\n" +
        "\uD83C\uDF59|rice ball\n" +
        "\uD83C\uDF5A|cooked rice\n" +
        "\uD83C\uDF5B|curry rice\n" +
        "\uD83C\uDF5C|steaming bowl ramen\n" +
        "\uD83C\uDF5D|spaghetti\n" +
        "\uD83C\uDF5E|bread\n" +
        "\uD83C\uDF5F|french fries\n" +
        "\uD83C\uDF60|roasted sweet potato\n" +
        "\uD83C\uDF61|dango\n" +
        "\uD83C\uDF62|oden\n" +
        "\uD83C\uDF63|sushi\n" +
        "\uD83C\uDF64|fried shrimp\n" +
        "\uD83C\uDF65|fish cake with swirl\n" +
        "\uD83C\uDF66|soft ice cream icecream\n" +
        "\uD83C\uDF67|shaved ice\n" +
        "\uD83C\uDF68|ice cream\n" +
        "\uD83C\uDF69|doughnut\n" +
        "\uD83C\uDF6A|cookie\n" +
        "\uD83C\uDF6B|chocolate bar\n" +
        "\uD83C\uDF6C|candy\n" +
        "\uD83C\uDF6D|lollipop\n" +
        "\uD83C\uDF6E|custard\n" +
        "\uD83C\uDF6F|honey pot\n" +
        "\uD83C\uDF70|shortcake cake\n" +
        "\uD83C\uDF71|bento box\n" +
        "\uD83C\uDF72|pot of food stew\n" +
        "\uD83C\uDF73|cooking fried egg\n" +
        "\uD83C\uDF74|fork and knife\n" +
        "\uD83C\uDF75|teacup without handle tea\n" +
        "\uD83C\uDF76|sake\n" +
        "\uD83C\uDF77|wine glass\n" +
        "\uD83C\uDF78|cocktail glass\n" +
        "\uD83C\uDF79|tropical drink\n" +
        "\uD83C\uDF7A|beer mug\n" +
        "\uD83C\uDF7B|clinking beer mugs beers\n" +
        "\uD83C\uDF7C|baby bottle\n" +
        "\uD83C\uDF7D\uFE0F|fork and knife with plate cutlery khabar khawa food\n" +
        "\uD83C\uDF7E|bottle with popping cork champagne\n" +
        "\uD83C\uDF7F|popcorn\n" +
        "\uD83C\uDF80|ribbon\n" +
        "\uD83C\uDF81|wrapped gift\n" +
        "\uD83C\uDF82|birthday cake jonmodin\n" +
        "\uD83C\uDF83|jack o lantern\n" +
        "\uD83C\uDF84|christmas tree\n" +
        "\uD83C\uDF85|santa claus\n" +
        "\uD83C\uDF86|fireworks\n" +
        "\uD83C\uDF87|sparkler\n" +
        "\uD83C\uDF88|balloon\n" +
        "\uD83C\uDF89|party popper tada utsob celebrate\n" +
        "\uD83C\uDF8A|confetti ball\n" +
        "\uD83C\uDF8B|tanabata tree\n" +
        "\uD83C\uDF8C|crossed flags\n" +
        "\uD83C\uDF8D|pine decoration bamboo\n" +
        "\uD83C\uDF8E|japanese dolls\n" +
        "\uD83C\uDF8F|carp streamer flags\n" +
        "\uD83C\uDF90|wind chime\n" +
        "\uD83C\uDF91|moon viewing ceremony scene rice\n" +
        "\uD83C\uDF92|backpack satchel school\n" +
        "\uD83C\uDF93|graduation cap mortar board\n" +
        "\uD83C\uDF96\uFE0F|military medal\n" +
        "\uD83C\uDF97\uFE0F|reminder ribbon\n" +
        "\uD83C\uDF99\uFE0F|studio microphone\n" +
        "\uD83C\uDF9A\uFE0F|level slider\n" +
        "\uD83C\uDF9B\uFE0F|control knobs\n" +
        "\uD83C\uDF9E\uFE0F|film frames strip\n" +
        "\uD83C\uDF9F\uFE0F|admission tickets\n" +
        "\uD83C\uDFA0|carousel horse\n" +
        "\uD83C\uDFA1|ferris wheel\n" +
        "\uD83C\uDFA2|roller coaster\n" +
        "\uD83C\uDFA3|fishing pole fish and\n" +
        "\uD83C\uDFA4|microphone\n" +
        "\uD83C\uDFA5|movie camera\n" +
        "\uD83C\uDFA6|cinema\n" +
        "\uD83C\uDFA7|headphone headphones\n" +
        "\uD83C\uDFA8|artist palette art\n" +
        "\uD83C\uDFA9|top hat tophat\n" +
        "\uD83C\uDFAA|circus tent\n" +
        "\uD83C\uDFAB|ticket\n" +
        "\uD83C\uDFAC|clapper board\n" +
        "\uD83C\uDFAD|performing arts\n" +
        "\uD83C\uDFAE|video game\n" +
        "\uD83C\uDFAF|bullseye dart\n" +
        "\uD83C\uDFB0|slot machine\n" +
        "\uD83C\uDFB1|pool 8 ball 8ball\n" +
        "\uD83C\uDFB2|game die\n" +
        "\uD83C\uDFB3|bowling\n" +
        "\uD83C\uDFB4|flower playing cards\n" +
        "\uD83C\uDFB5|musical note\n" +
        "\uD83C\uDFB6|musical notes\n" +
        "\uD83C\uDFB7|saxophone\n" +
        "\uD83C\uDFB8|guitar\n" +
        "\uD83C\uDFB9|musical keyboard\n" +
        "\uD83C\uDFBA|trumpet\n" +
        "\uD83C\uDFBB|violin\n" +
        "\uD83C\uDFBC|musical score\n" +
        "\uD83C\uDFBD|running shirt sash with\n" +
        "\uD83C\uDFBE|tennis\n" +
        "\uD83C\uDFBF|skis ski\n" +
        "\uD83C\uDFC0|basketball\n" +
        "\uD83C\uDFC1|chequered flag checkered\n" +
        "\uD83C\uDFC2|snowboarder\n" +
        "\uD83C\uDFC3|person running runner\n" +
        "\uD83C\uDFC3\u200D\u2642\uFE0F|man running\n" +
        "\uD83C\uDFC3\u200D\u2640\uFE0F|woman running\n" +
        "\uD83C\uDFC4|person surfing surfer\n" +
        "\uD83C\uDFC4\u200D\u2642\uFE0F|man surfing\n" +
        "\uD83C\uDFC4\u200D\u2640\uFE0F|woman surfing\n" +
        "\uD83C\uDFC5|sports medal\n" +
        "\uD83C\uDFC6|trophy\n" +
        "\uD83C\uDFC7|horse racing\n" +
        "\uD83C\uDFC8|american football\n" +
        "\uD83C\uDFC9|rugby football\n" +
        "\uD83C\uDFCA|person swimming swimmer\n" +
        "\uD83C\uDFCA\u200D\u2642\uFE0F|man swimming\n" +
        "\uD83C\uDFCA\u200D\u2640\uFE0F|woman swimming\n" +
        "\uD83C\uDFCB\uFE0F|person lifting weights weight\n" +
        "\uD83C\uDFCB\uFE0F\u200D\u2642\uFE0F|man lifting weights weight\n" +
        "\uD83C\uDFCB\uFE0F\u200D\u2640\uFE0F|woman lifting weights weight\n" +
        "\uD83C\uDFCC\uFE0F|person golfing\n" +
        "\uD83C\uDFCC\uFE0F\u200D\u2642\uFE0F|man golfing\n" +
        "\uD83C\uDFCC\uFE0F\u200D\u2640\uFE0F|woman golfing\n" +
        "\uD83C\uDFCD\uFE0F|motorcycle\n" +
        "\uD83C\uDFCE\uFE0F|racing car\n" +
        "\uD83C\uDFCF|cricket game\n" +
        "\uD83C\uDFD0|volleyball\n" +
        "\uD83C\uDFD1|field hockey\n" +
        "\uD83C\uDFD2|ice hockey\n" +
        "\uD83C\uDFD3|ping pong\n" +
        "\uD83C\uDFD4\uFE0F|snow capped mountain\n" +
        "\uD83C\uDFD5\uFE0F|camping\n" +
        "\uD83C\uDFD6\uFE0F|beach with umbrella\n" +
        "\uD83C\uDFD7\uFE0F|building construction\n" +
        "\uD83C\uDFD8\uFE0F|houses\n" +
        "\uD83C\uDFD9\uFE0F|cityscape\n" +
        "\uD83C\uDFDA\uFE0F|derelict house\n" +
        "\uD83C\uDFDB\uFE0F|classical building\n" +
        "\uD83C\uDFDC\uFE0F|desert\n" +
        "\uD83C\uDFDD\uFE0F|desert island\n" +
        "\uD83C\uDFDE\uFE0F|national park\n" +
        "\uD83C\uDFDF\uFE0F|stadium\n" +
        "\uD83C\uDFE0|house bari ghor home\n" +
        "\uD83C\uDFE1|house with garden\n" +
        "\uD83C\uDFE2|office building\n" +
        "\uD83C\uDFE3|japanese post office\n" +
        "\uD83C\uDFE4|post office european\n" +
        "\uD83C\uDFE5|hospital\n" +
        "\uD83C\uDFE6|bank\n" +
        "\uD83C\uDFE7|atm sign\n" +
        "\uD83C\uDFE8|hotel\n" +
        "\uD83C\uDFE9|love hotel\n" +
        "\uD83C\uDFEA|convenience store\n" +
        "\uD83C\uDFEB|school\n" +
        "\uD83C\uDFEC|department store\n" +
        "\uD83C\uDFED|factory\n" +
        "\uD83C\uDFEE|red paper lantern izakaya\n" +
        "\uD83C\uDFEF|japanese castle\n" +
        "\uD83C\uDFF0|castle european\n" +
        "\uD83C\uDFF3\uFE0F|white flag\n" +
        "\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08|rainbow flag\n" +
        "\uD83C\uDFF3\uFE0F\u200D\u26A7\uFE0F|transgender flag\n" +
        "\uD83C\uDFF4|black flag\n" +
        "\uD83C\uDFF4\u200D\u2620\uFE0F|pirate flag\n" +
        "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F|flag england\n" +
        "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F|flag scotland\n" +
        "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F|flag wales\n" +
        "\uD83C\uDFF5\uFE0F|rosette\n" +
        "\uD83C\uDFF7\uFE0F|label\n" +
        "\uD83C\uDFF8|badminton\n" +
        "\uD83C\uDFF9|bow and arrow\n" +
        "\uD83C\uDFFA|amphora\n" +
        "\uD83D\uDC00|rat\n" +
        "\uD83D\uDC01|mouse mouse2\n" +
        "\uD83D\uDC02|ox\n" +
        "\uD83D\uDC03|water buffalo\n" +
        "\uD83D\uDC04|cow cow2\n" +
        "\uD83D\uDC05|tiger tiger2\n" +
        "\uD83D\uDC06|leopard\n" +
        "\uD83D\uDC07|rabbit rabbit2\n" +
        "\uD83D\uDC08|cat cat2\n" +
        "\uD83D\uDC08\u200D\u2B1B|black cat\n" +
        "\uD83D\uDC09|dragon\n" +
        "\uD83D\uDC0A|crocodile\n" +
        "\uD83D\uDC0B|whale whale2\n" +
        "\uD83D\uDC0C|snail\n" +
        "\uD83D\uDC0D|snake\n" +
        "\uD83D\uDC0E|horse racehorse\n" +
        "\uD83D\uDC0F|ram\n" +
        "\uD83D\uDC10|goat\n" +
        "\uD83D\uDC11|ewe sheep\n" +
        "\uD83D\uDC12|monkey\n" +
        "\uD83D\uDC13|rooster\n" +
        "\uD83D\uDC14|chicken\n" +
        "\uD83D\uDC15|dog dog2\n" +
        "\uD83D\uDC15\u200D\uD83E\uDDBA|service dog\n" +
        "\uD83D\uDC16|pig pig2\n" +
        "\uD83D\uDC17|boar\n" +
        "\uD83D\uDC18|elephant\n" +
        "\uD83D\uDC19|octopus\n" +
        "\uD83D\uDC1A|spiral shell\n" +
        "\uD83D\uDC1B|bug\n" +
        "\uD83D\uDC1C|ant\n" +
        "\uD83D\uDC1D|honeybee bee\n" +
        "\uD83D\uDC1E|lady beetle\n" +
        "\uD83D\uDC1F|fish\n" +
        "\uD83D\uDC20|tropical fish\n" +
        "\uD83D\uDC21|blowfish\n" +
        "\uD83D\uDC22|turtle\n" +
        "\uD83D\uDC23|hatching chick\n" +
        "\uD83D\uDC24|baby chick\n" +
        "\uD83D\uDC25|front facing baby chick hatched\n" +
        "\uD83D\uDC26|bird\n" +
        "\uD83D\uDC26\u200D\u2B1B|black bird\n" +
        "\uD83D\uDC27|penguin\n" +
        "\uD83D\uDC28|koala\n" +
        "\uD83D\uDC29|poodle\n" +
        "\uD83D\uDC2A|camel dromedary\n" +
        "\uD83D\uDC2B|two hump camel\n"

    private val P3: String =
        "\uD83D\uDC2C|dolphin\n" +
        "\uD83D\uDC2D|mouse face\n" +
        "\uD83D\uDC2E|cow face\n" +
        "\uD83D\uDC2F|tiger face\n" +
        "\uD83D\uDC30|rabbit face\n" +
        "\uD83D\uDC31|cat face\n" +
        "\uD83D\uDC32|dragon face\n" +
        "\uD83D\uDC33|spouting whale\n" +
        "\uD83D\uDC34|horse face\n" +
        "\uD83D\uDC35|monkey face\n" +
        "\uD83D\uDC36|dog face\n" +
        "\uD83D\uDC37|pig face\n" +
        "\uD83D\uDC38|frog\n" +
        "\uD83D\uDC39|hamster\n" +
        "\uD83D\uDC3A|wolf\n" +
        "\uD83D\uDC3B|bear\n" +
        "\uD83D\uDC3B\u200D\u2744\uFE0F|polar bear\n" +
        "\uD83D\uDC3C|panda face\n" +
        "\uD83D\uDC3D|pig nose\n" +
        "\uD83D\uDC3E|paw prints feet\n" +
        "\uD83D\uDC3F\uFE0F|chipmunk\n" +
        "\uD83D\uDC40|eyes\n" +
        "\uD83D\uDC41\uFE0F|eye\n" +
        "\uD83D\uDC41\uFE0F\u200D\uD83D\uDDE8\uFE0F|eye in speech bubble\n" +
        "\uD83D\uDC42|ear\n" +
        "\uD83D\uDC43|nose\n" +
        "\uD83D\uDC44|mouth lips\n" +
        "\uD83D\uDC45|tongue\n" +
        "\uD83D\uDC46|backhand index pointing up point 2\n" +
        "\uD83D\uDC47|backhand index pointing down point\n" +
        "\uD83D\uDC48|backhand index pointing left point\n" +
        "\uD83D\uDC49|backhand index pointing right point\n" +
        "\uD83D\uDC4A|oncoming fist\n" +
        "\uD83D\uDC4B|waving hand wave\n" +
        "\uD83D\uDC4C|ok hand\n" +
        "\uD83D\uDC4D|thumbs up thik ache bhalo accha ok like\n" +
        "\uD83D\uDC4E|thumbs down kharap dislike\n" +
        "\uD83D\uDC4F|clapping hands clap tali shabash\n" +
        "\uD83D\uDC50|open hands\n" +
        "\uD83D\uDC51|crown\n" +
        "\uD83D\uDC52|woman\u2019s hat womans\n" +
        "\uD83D\uDC53|glasses eyeglasses\n" +
        "\uD83D\uDC54|necktie\n" +
        "\uD83D\uDC55|t shirt\n" +
        "\uD83D\uDC56|jeans\n" +
        "\uD83D\uDC57|dress\n" +
        "\uD83D\uDC58|kimono\n" +
        "\uD83D\uDC59|bikini\n" +
        "\uD83D\uDC5A|woman\u2019s clothes womans\n" +
        "\uD83D\uDC5B|purse\n" +
        "\uD83D\uDC5C|handbag\n" +
        "\uD83D\uDC5D|clutch bag pouch\n" +
        "\uD83D\uDC5E|man\u2019s shoe mans\n" +
        "\uD83D\uDC5F|running shoe athletic\n" +
        "\uD83D\uDC60|high heeled shoe heel\n" +
        "\uD83D\uDC61|woman\u2019s sandal\n" +
        "\uD83D\uDC62|woman\u2019s boot\n" +
        "\uD83D\uDC63|footprints\n" +
        "\uD83D\uDC64|bust in silhouette\n" +
        "\uD83D\uDC65|busts in silhouette\n" +
        "\uD83D\uDC66|boy\n" +
        "\uD83D\uDC67|girl\n" +
        "\uD83D\uDC68|man\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDB2|man bald\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDB1|man curly hair haired\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC66|family man boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC67|family man girl\n" +
        "\uD83D\uDC68\u200D\uD83C\uDFA8|man artist\n" +
        "\uD83D\uDC68\u200D\uD83D\uDE80|man astronaut\n" +
        "\uD83D\uDC68\u200D\uD83C\uDF73|man cook\n" +
        "\uD83D\uDC68\u200D\uD83C\uDFED|man factory worker\n" +
        "\uD83D\uDC68\u200D\uD83C\uDF3E|man farmer\n" +
        "\uD83D\uDC68\u200D\uD83C\uDF7C|man feeding baby\n" +
        "\uD83D\uDC68\u200D\uD83D\uDE92|man firefighter\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDBD|man in manual wheelchair\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDBC|man in motorized wheelchair\n" +
        "\uD83D\uDC68\u200D\uD83D\uDD27|man mechanic\n" +
        "\uD83D\uDC68\u200D\uD83D\uDCBC|man office worker\n" +
        "\uD83D\uDC68\u200D\uD83D\uDD2C|man scientist\n" +
        "\uD83D\uDC68\u200D\uD83C\uDFA4|man singer\n" +
        "\uD83D\uDC68\u200D\uD83C\uDF93|man student\n" +
        "\uD83D\uDC68\u200D\uD83C\uDFEB|man teacher\n" +
        "\uD83D\uDC68\u200D\uD83D\uDCBB|man technologist\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDAF|man with white cane probing\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDB0|man red hair haired\n" +
        "\uD83D\uDC68\u200D\uD83E\uDDB3|man white hair haired\n" +
        "\uD83D\uDC68\u200D\u2695\uFE0F|man health worker\n" +
        "\uD83D\uDC68\u200D\u2696\uFE0F|man judge\n" +
        "\uD83D\uDC68\u200D\u2708\uFE0F|man pilot\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66|family man boy boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66|family man girl boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67|family man girl girl\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66|family man man boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67|family man man girl\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66|family man woman boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67|family man woman girl\n" +
        "\uD83D\uDC68\u200D\u2764\uFE0F\u200D\uD83D\uDC68|couple with heart man man\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66|family man man boy boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66|family man man girl boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67|family man man girl girl\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66|family man woman boy boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66|family man woman girl boy\n" +
        "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67|family man woman girl girl\n" +
        "\uD83D\uDC68\u200D\u2764\uFE0F\u200D\uD83D\uDC8B\u200D\uD83D\uDC68|kiss man man couplekiss\n" +
        "\uD83D\uDC69|woman\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDB2|woman bald\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDB1|woman curly hair haired\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC66|family woman boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC67|family woman girl\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDB0|woman red hair haired\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDB3|woman white hair haired\n" +
        "\uD83D\uDC69\u200D\uD83C\uDFA8|woman artist\n" +
        "\uD83D\uDC69\u200D\uD83D\uDE80|woman astronaut\n" +
        "\uD83D\uDC69\u200D\uD83C\uDF73|woman cook\n" +
        "\uD83D\uDC69\u200D\uD83C\uDFED|woman factory worker\n" +
        "\uD83D\uDC69\u200D\uD83C\uDF3E|woman farmer\n" +
        "\uD83D\uDC69\u200D\uD83C\uDF7C|woman feeding baby\n" +
        "\uD83D\uDC69\u200D\uD83D\uDE92|woman firefighter\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDBD|woman in manual wheelchair\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDBC|woman in motorized wheelchair\n" +
        "\uD83D\uDC69\u200D\uD83D\uDD27|woman mechanic\n" +
        "\uD83D\uDC69\u200D\uD83D\uDCBC|woman office worker\n" +
        "\uD83D\uDC69\u200D\uD83D\uDD2C|woman scientist\n" +
        "\uD83D\uDC69\u200D\uD83C\uDFA4|woman singer\n" +
        "\uD83D\uDC69\u200D\uD83C\uDF93|woman student\n" +
        "\uD83D\uDC69\u200D\uD83C\uDFEB|woman teacher\n" +
        "\uD83D\uDC69\u200D\uD83D\uDCBB|woman technologist\n" +
        "\uD83D\uDC69\u200D\uD83E\uDDAF|woman with white cane probing\n" +
        "\uD83D\uDC69\u200D\u2695\uFE0F|woman health worker\n" +
        "\uD83D\uDC69\u200D\u2696\uFE0F|woman judge\n" +
        "\uD83D\uDC69\u200D\u2708\uFE0F|woman pilot\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66|family woman boy boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66|family woman girl boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67|family woman girl girl\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66|family woman woman boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67|family woman woman girl\n" +
        "\uD83D\uDC69\u200D\u2764\uFE0F\u200D\uD83D\uDC68|couple with heart woman man\n" +
        "\uD83D\uDC69\u200D\u2764\uFE0F\u200D\uD83D\uDC69|couple with heart woman woman\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66|family woman woman boy boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66|family woman woman girl boy\n" +
        "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67|family woman woman girl girl\n" +
        "\uD83D\uDC69\u200D\u2764\uFE0F\u200D\uD83D\uDC8B\u200D\uD83D\uDC68|kiss woman man couplekiss\n" +
        "\uD83D\uDC69\u200D\u2764\uFE0F\u200D\uD83D\uDC8B\u200D\uD83D\uDC69|kiss woman woman couplekiss\n" +
        "\uD83D\uDC6A|family\n" +
        "\uD83D\uDC6B|woman and man holding hands couple\n" +
        "\uD83D\uDC6C|men holding hands two\n" +
        "\uD83D\uDC6D|women holding hands two\n" +
        "\uD83D\uDC6E|police officer\n" +
        "\uD83D\uDC6E\u200D\u2642\uFE0F|man police officer policeman\n" +
        "\uD83D\uDC6E\u200D\u2640\uFE0F|woman police officer policewoman\n" +
        "\uD83D\uDC6F|people with bunny ears dancers\n" +
        "\uD83D\uDC6F\u200D\u2642\uFE0F|men with bunny ears dancing\n" +
        "\uD83D\uDC6F\u200D\u2640\uFE0F|women with bunny ears dancing\n" +
        "\uD83D\uDC70|person with veil\n" +
        "\uD83D\uDC70\u200D\u2642\uFE0F|man with veil\n" +
        "\uD83D\uDC70\u200D\u2640\uFE0F|woman with veil\n" +
        "\uD83D\uDC71|person blond hair haired\n" +
        "\uD83D\uDC71\u200D\u2642\uFE0F|man blond hair haired\n" +
        "\uD83D\uDC71\u200D\u2640\uFE0F|woman blond hair haired\n" +
        "\uD83D\uDC72|person with skullcap pi gua mao man\n" +
        "\uD83D\uDC73|person wearing turban with\n" +
        "\uD83D\uDC73\u200D\u2642\uFE0F|man wearing turban with\n" +
        "\uD83D\uDC73\u200D\u2640\uFE0F|woman wearing turban with\n" +
        "\uD83D\uDC74|old man older\n" +
        "\uD83D\uDC75|old woman older\n" +
        "\uD83D\uDC76|baby\n" +
        "\uD83D\uDC77|construction worker\n" +
        "\uD83D\uDC77\u200D\u2642\uFE0F|man construction worker\n" +
        "\uD83D\uDC77\u200D\u2640\uFE0F|woman construction worker\n" +
        "\uD83D\uDC78|princess\n" +
        "\uD83D\uDC79|ogre japanese\n" +
        "\uD83D\uDC7A|goblin japanese\n" +
        "\uD83D\uDC7B|ghost\n" +
        "\uD83D\uDC7C|baby angel\n" +
        "\uD83D\uDC7D|alien\n" +
        "\uD83D\uDC7E|alien monster invader space\n" +
        "\uD83D\uDC7F|angry face with horns imp\n" +
        "\uD83D\uDC80|skull\n" +
        "\uD83D\uDC81|person tipping hand\n" +
        "\uD83D\uDC81\u200D\u2642\uFE0F|man tipping hand\n" +
        "\uD83D\uDC81\u200D\u2640\uFE0F|woman tipping hand\n" +
        "\uD83D\uDC82|guard\n" +
        "\uD83D\uDC82\u200D\u2642\uFE0F|man guard guardsman\n" +
        "\uD83D\uDC82\u200D\u2640\uFE0F|woman guard guardswoman\n" +
        "\uD83D\uDC83|woman dancing\n" +
        "\uD83D\uDC84|lipstick\n" +
        "\uD83D\uDC85|nail polish care\n" +
        "\uD83D\uDC86|person getting massage\n" +
        "\uD83D\uDC86\u200D\u2642\uFE0F|man getting massage\n" +
        "\uD83D\uDC86\u200D\u2640\uFE0F|woman getting massage\n" +
        "\uD83D\uDC87|person getting haircut\n" +
        "\uD83D\uDC87\u200D\u2642\uFE0F|man getting haircut\n" +
        "\uD83D\uDC87\u200D\u2640\uFE0F|woman getting haircut\n" +
        "\uD83D\uDC88|barber pole\n" +
        "\uD83D\uDC89|syringe\n" +
        "\uD83D\uDC8A|pill\n" +
        "\uD83D\uDC8B|kiss mark\n" +
        "\uD83D\uDC8C|love letter\n" +
        "\uD83D\uDC8D|ring\n" +
        "\uD83D\uDC8E|gem stone\n" +
        "\uD83D\uDC8F|kiss couplekiss\n" +
        "\uD83D\uDC90|bouquet\n" +
        "\uD83D\uDC91|couple with heart\n" +
        "\uD83D\uDC92|wedding\n" +
        "\uD83D\uDC93|beating heart heartbeat\n" +
        "\uD83D\uDC94|broken heart dil bhanga kosto\n" +
        "\uD83D\uDC95|two hearts\n"

    private val P4: String =
        "\uD83D\uDC96|sparkling heart\n" +
        "\uD83D\uDC97|growing heart heartpulse\n" +
        "\uD83D\uDC98|heart with arrow cupid\n" +
        "\uD83D\uDC99|blue heart\n" +
        "\uD83D\uDC9A|green heart\n" +
        "\uD83D\uDC9B|yellow heart\n" +
        "\uD83D\uDC9C|purple heart\n" +
        "\uD83D\uDC9D|heart with ribbon gift\n" +
        "\uD83D\uDC9E|revolving hearts\n" +
        "\uD83D\uDC9F|heart decoration\n" +
        "\uD83D\uDCA0|diamond with a dot inside shape\n" +
        "\uD83D\uDCA1|light bulb\n" +
        "\uD83D\uDCA2|anger symbol\n" +
        "\uD83D\uDCA3|bomb\n" +
        "\uD83D\uDCA4|zzz\n" +
        "\uD83D\uDCA5|collision boom\n" +
        "\uD83D\uDCA6|sweat droplets drops\n" +
        "\uD83D\uDCA7|droplet\n" +
        "\uD83D\uDCA8|dashing away dash\n" +
        "\uD83D\uDCA9|pile of poo hankey\n" +
        "\uD83D\uDCAA|flexed biceps muscle\n" +
        "\uD83D\uDCAB|dizzy\n" +
        "\uD83D\uDCAC|speech balloon\n" +
        "\uD83D\uDCAD|thought balloon\n" +
        "\uD83D\uDCAE|white flower\n" +
        "\uD83D\uDCAF|hundred points 100\n" +
        "\uD83D\uDCB0|money bag moneybag\n" +
        "\uD83D\uDCB1|currency exchange\n" +
        "\uD83D\uDCB2|heavy dollar sign\n" +
        "\uD83D\uDCB3|credit card\n" +
        "\uD83D\uDCB4|yen banknote\n" +
        "\uD83D\uDCB5|dollar banknote\n" +
        "\uD83D\uDCB6|euro banknote\n" +
        "\uD83D\uDCB7|pound banknote\n" +
        "\uD83D\uDCB8|money with wings\n" +
        "\uD83D\uDCB9|chart increasing with yen\n" +
        "\uD83D\uDCBA|seat\n" +
        "\uD83D\uDCBB|laptop computer code\n" +
        "\uD83D\uDCBC|briefcase\n" +
        "\uD83D\uDCBD|computer disk minidisc\n" +
        "\uD83D\uDCBE|floppy disk\n" +
        "\uD83D\uDCBF|optical disk cd\n" +
        "\uD83D\uDCC0|dvd\n" +
        "\uD83D\uDCC1|file folder\n" +
        "\uD83D\uDCC2|open file folder\n" +
        "\uD83D\uDCC3|page with curl\n" +
        "\uD83D\uDCC4|page facing up\n" +
        "\uD83D\uDCC5|calendar date\n" +
        "\uD83D\uDCC6|tear off calendar\n" +
        "\uD83D\uDCC7|card index\n" +
        "\uD83D\uDCC8|chart increasing upwards with trend\n" +
        "\uD83D\uDCC9|chart decreasing with trend downwards\n" +
        "\uD83D\uDCCA|bar chart\n" +
        "\uD83D\uDCCB|clipboard\n" +
        "\uD83D\uDCCC|pushpin\n" +
        "\uD83D\uDCCD|round pushpin\n" +
        "\uD83D\uDCCE|paperclip\n" +
        "\uD83D\uDCCF|straight ruler\n" +
        "\uD83D\uDCD0|triangular ruler\n" +
        "\uD83D\uDCD1|bookmark tabs\n" +
        "\uD83D\uDCD2|ledger\n" +
        "\uD83D\uDCD3|notebook\n" +
        "\uD83D\uDCD4|notebook with decorative cover\n" +
        "\uD83D\uDCD5|closed book\n" +
        "\uD83D\uDCD6|open book\n" +
        "\uD83D\uDCD7|green book\n" +
        "\uD83D\uDCD8|blue book\n" +
        "\uD83D\uDCD9|orange book\n" +
        "\uD83D\uDCDA|books boi pora study book\n" +
        "\uD83D\uDCDB|name badge\n" +
        "\uD83D\uDCDC|scroll\n" +
        "\uD83D\uDCDD|memo\n" +
        "\uD83D\uDCDE|telephone receiver\n" +
        "\uD83D\uDCDF|pager\n" +
        "\uD83D\uDCE0|fax machine\n" +
        "\uD83D\uDCE1|satellite antenna\n" +
        "\uD83D\uDCE2|loudspeaker\n" +
        "\uD83D\uDCE3|megaphone mega\n" +
        "\uD83D\uDCE4|outbox tray\n" +
        "\uD83D\uDCE5|inbox tray\n" +
        "\uD83D\uDCE6|package\n" +
        "\uD83D\uDCE7|e mail email\n" +
        "\uD83D\uDCE8|incoming envelope\n" +
        "\uD83D\uDCE9|envelope with arrow\n" +
        "\uD83D\uDCEA|closed mailbox with lowered flag\n" +
        "\uD83D\uDCEB|closed mailbox with raised flag\n" +
        "\uD83D\uDCEC|open mailbox with raised flag mail\n" +
        "\uD83D\uDCED|open mailbox with lowered flag no mail\n" +
        "\uD83D\uDCEE|postbox\n" +
        "\uD83D\uDCEF|postal horn\n" +
        "\uD83D\uDCF0|newspaper\n" +
        "\uD83D\uDCF1|mobile phone iphone\n" +
        "\uD83D\uDCF2|mobile phone with arrow calling\n" +
        "\uD83D\uDCF3|vibration mode\n" +
        "\uD83D\uDCF4|mobile phone off\n" +
        "\uD83D\uDCF5|no mobile phones\n" +
        "\uD83D\uDCF6|antenna bars strength signal\n" +
        "\uD83D\uDCF7|camera\n" +
        "\uD83D\uDCF8|camera with flash\n" +
        "\uD83D\uDCF9|video camera\n" +
        "\uD83D\uDCFA|television tv\n" +
        "\uD83D\uDCFB|radio\n" +
        "\uD83D\uDCFC|videocassette vhs\n" +
        "\uD83D\uDCFD\uFE0F|film projector\n" +
        "\uD83D\uDCFF|prayer beads\n" +
        "\uD83D\uDD00|shuffle tracks button rightwards twisted arrows\n" +
        "\uD83D\uDD01|repeat button\n" +
        "\uD83D\uDD02|repeat single button one\n" +
        "\uD83D\uDD03|clockwise vertical arrows\n" +
        "\uD83D\uDD04|counterclockwise arrows button\n" +
        "\uD83D\uDD05|dim button low brightness\n" +
        "\uD83D\uDD06|bright button high brightness\n" +
        "\uD83D\uDD07|muted speaker mute\n" +
        "\uD83D\uDD08|speaker low volume\n" +
        "\uD83D\uDD09|speaker medium volume sound\n" +
        "\uD83D\uDD0A|speaker high volume loud sound\n" +
        "\uD83D\uDD0B|battery\n" +
        "\uD83D\uDD0C|electric plug\n" +
        "\uD83D\uDD0D|magnifying glass tilted left mag\n" +
        "\uD83D\uDD0E|magnifying glass tilted right mag\n" +
        "\uD83D\uDD0F|locked with pen ink lock\n" +
        "\uD83D\uDD10|locked with key lock closed\n" +
        "\uD83D\uDD11|key\n" +
        "\uD83D\uDD12|locked lock\n" +
        "\uD83D\uDD13|unlocked unlock\n" +
        "\uD83D\uDD14|bell\n" +
        "\uD83D\uDD15|bell with slash no\n" +
        "\uD83D\uDD16|bookmark\n" +
        "\uD83D\uDD17|link\n" +
        "\uD83D\uDD18|radio button\n" +
        "\uD83D\uDD19|back arrow\n" +
        "\uD83D\uDD1A|end arrow\n" +
        "\uD83D\uDD1B|on! arrow on\n" +
        "\uD83D\uDD1C|soon arrow\n" +
        "\uD83D\uDD1D|top arrow\n" +
        "\uD83D\uDD1E|no one under eighteen underage\n" +
        "\uD83D\uDD1F|keycap 10 ten\n" +
        "\uD83D\uDD20|input latin uppercase capital abcd\n" +
        "\uD83D\uDD21|input latin lowercase abcd\n" +
        "\uD83D\uDD22|input numbers 1234\n" +
        "\uD83D\uDD23|input symbols\n" +
        "\uD83D\uDD24|input latin letters abc\n" +
        "\uD83D\uDD25|fire\n" +
        "\uD83D\uDD26|flashlight\n" +
        "\uD83D\uDD27|wrench\n" +
        "\uD83D\uDD28|hammer\n" +
        "\uD83D\uDD29|nut and bolt\n" +
        "\uD83D\uDD2A|kitchen knife hocho\n" +
        "\uD83D\uDD2B|water pistol gun\n" +
        "\uD83D\uDD2C|microscope\n" +
        "\uD83D\uDD2D|telescope\n" +
        "\uD83D\uDD2E|crystal ball\n" +
        "\uD83D\uDD2F|dotted six pointed star\n" +
        "\uD83D\uDD30|japanese symbol for beginner\n" +
        "\uD83D\uDD31|trident emblem\n" +
        "\uD83D\uDD32|black square button\n" +
        "\uD83D\uDD33|white square button\n" +
        "\uD83D\uDD34|red circle\n" +
        "\uD83D\uDD35|blue circle large\n" +
        "\uD83D\uDD36|large orange diamond\n" +
        "\uD83D\uDD37|large blue diamond\n" +
        "\uD83D\uDD38|small orange diamond\n" +
        "\uD83D\uDD39|small blue diamond\n" +
        "\uD83D\uDD3A|red triangle pointed up small\n" +
        "\uD83D\uDD3B|red triangle pointed down small\n" +
        "\uD83D\uDD3C|upwards button up small arrow\n" +
        "\uD83D\uDD3D|downwards button small arrow down\n" +
        "\uD83D\uDD49\uFE0F|om\n" +
        "\uD83D\uDD4A\uFE0F|dove\n" +
        "\uD83D\uDD4B|kaaba\n" +
        "\uD83D\uDD4C|mosque\n" +
        "\uD83D\uDD4D|synagogue\n" +
        "\uD83D\uDD4E|menorah\n" +
        "\uD83D\uDD50|one o\u2019clock clock1\n" +
        "\uD83D\uDD51|two o\u2019clock clock2\n" +
        "\uD83D\uDD52|three o\u2019clock clock3\n" +
        "\uD83D\uDD53|four o\u2019clock clock4\n" +
        "\uD83D\uDD54|five o\u2019clock clock5\n" +
        "\uD83D\uDD55|six o\u2019clock clock6\n" +
        "\uD83D\uDD56|seven o\u2019clock clock7\n" +
        "\uD83D\uDD57|eight o\u2019clock clock8\n" +
        "\uD83D\uDD58|nine o\u2019clock clock9\n" +
        "\uD83D\uDD59|ten o\u2019clock clock10\n" +
        "\uD83D\uDD5A|eleven o\u2019clock clock11\n" +
        "\uD83D\uDD5B|twelve o\u2019clock clock12\n" +
        "\uD83D\uDD5C|one thirty clock130\n" +
        "\uD83D\uDD5D|two thirty clock230\n" +
        "\uD83D\uDD5E|three thirty clock330\n" +
        "\uD83D\uDD5F|four thirty clock430\n" +
        "\uD83D\uDD60|five thirty clock530\n" +
        "\uD83D\uDD61|six thirty clock630\n" +
        "\uD83D\uDD62|seven thirty clock730\n" +
        "\uD83D\uDD63|eight thirty clock830\n" +
        "\uD83D\uDD64|nine thirty clock930\n" +
        "\uD83D\uDD65|ten thirty clock1030\n" +
        "\uD83D\uDD66|eleven thirty clock1130\n" +
        "\uD83D\uDD67|twelve thirty clock1230\n" +
        "\uD83D\uDD6F\uFE0F|candle\n" +
        "\uD83D\uDD70\uFE0F|mantelpiece clock\n" +
        "\uD83D\uDD73\uFE0F|hole\n" +
        "\uD83D\uDD74\uFE0F|person in suit levitating business\n" +
        "\uD83D\uDD75\uFE0F|detective\n" +
        "\uD83D\uDD75\uFE0F\u200D\u2640\uFE0F|woman detective female\n" +
        "\uD83D\uDD75\uFE0F\u200D\u2642\uFE0F|man detective male\n" +
        "\uD83D\uDD76\uFE0F|sunglasses dark\n" +
        "\uD83D\uDD77\uFE0F|spider\n" +
        "\uD83D\uDD78\uFE0F|spider web\n" +
        "\uD83D\uDD79\uFE0F|joystick\n" +
        "\uD83D\uDD7A|man dancing\n" +
        "\uD83D\uDD87\uFE0F|linked paperclips\n" +
        "\uD83D\uDD8A\uFE0F|pen\n" +
        "\uD83D\uDD8B\uFE0F|fountain pen\n" +
        "\uD83D\uDD8C\uFE0F|paintbrush\n" +
        "\uD83D\uDD8D\uFE0F|crayon\n" +
        "\uD83D\uDD90\uFE0F|hand with fingers splayed raised\n" +
        "\uD83D\uDD95|middle finger\n" +
        "\uD83D\uDD96|vulcan salute\n" +
        "\uD83D\uDDA4|black heart\n" +
        "\uD83D\uDDA5\uFE0F|desktop computer\n" +
        "\uD83D\uDDA8\uFE0F|printer\n" +
        "\uD83D\uDDB1\uFE0F|computer mouse\n" +
        "\uD83D\uDDB2\uFE0F|trackball\n" +
        "\uD83D\uDDBC\uFE0F|framed picture\n" +
        "\uD83D\uDDC2\uFE0F|card index dividers\n" +
        "\uD83D\uDDC3\uFE0F|card file box\n" +
        "\uD83D\uDDC4\uFE0F|file cabinet\n" +
        "\uD83D\uDDD1\uFE0F|wastebasket\n" +
        "\uD83D\uDDD2\uFE0F|spiral notepad\n" +
        "\uD83D\uDDD3\uFE0F|spiral calendar\n" +
        "\uD83D\uDDDC\uFE0F|clamp\n" +
        "\uD83D\uDDDD\uFE0F|old key\n" +
        "\uD83D\uDDDE\uFE0F|rolled up newspaper roll\n" +
        "\uD83D\uDDE1\uFE0F|dagger\n" +
        "\uD83D\uDDE3\uFE0F|speaking head\n" +
        "\uD83D\uDDE8\uFE0F|left speech bubble\n" +
        "\uD83D\uDDEF\uFE0F|right anger bubble\n" +
        "\uD83D\uDDF3\uFE0F|ballot box with ballot\n" +
        "\uD83D\uDDFA\uFE0F|world map\n" +
        "\uD83D\uDDFB|mount fuji\n" +
        "\uD83D\uDDFC|tokyo tower\n" +
        "\uD83D\uDDFD|statue of liberty\n" +
        "\uD83D\uDDFE|map of japan\n" +
        "\uD83D\uDDFF|moai moyai\n" +
        "\uD83D\uDE00|grinning face hasi khushi\n" +
        "\uD83D\uDE01|beaming face with smiling eyes grin hasi daat\n" +
        "\uD83D\uDE02|face with tears of joy hasi hasa hasha lol funny moja\n" +
        "\uD83D\uDE03|grinning face with big eyes smiley\n" +
        "\uD83D\uDE04|grinning face with smiling eyes smile\n" +
        "\uD83D\uDE05|grinning face with sweat smile\n" +
        "\uD83D\uDE06|grinning squinting face laughing\n" +
        "\uD83D\uDE07|smiling face with halo innocent\n" +
        "\uD83D\uDE08|smiling face with horns imp\n" +
        "\uD83D\uDE09|winking face wink\n" +
        "\uD83D\uDE0A|smiling face with smiling eyes blush hasi khushi shukhi bhalo\n" +
        "\uD83D\uDE0B|face savoring food yum\n" +
        "\uD83D\uDE0C|relieved face\n" +
        "\uD83D\uDE0D|smiling face with heart eyes bhalobasha prem pyar love\n" +
        "\uD83D\uDE0E|smiling face with sunglasses\n" +
        "\uD83D\uDE0F|smirking face smirk\n" +
        "\uD83D\uDE10|neutral face\n" +
        "\uD83D\uDE11|expressionless face\n" +
        "\uD83D\uDE12|unamused face\n" +
        "\uD83D\uDE13|downcast face with sweat\n" +
        "\uD83D\uDE14|pensive face\n" +
        "\uD83D\uDE15|confused face\n" +
        "\uD83D\uDE16|confounded face\n" +
        "\uD83D\uDE17|kissing face\n" +
        "\uD83D\uDE18|face blowing a kiss kissing heart chumu bhalobasha\n" +
        "\uD83D\uDE19|kissing face with smiling eyes\n" +
        "\uD83D\uDE1A|kissing face with closed eyes\n" +
        "\uD83D\uDE1B|face with tongue out stuck\n" +
        "\uD83D\uDE1C|winking face with tongue out stuck eye\n"

    private val P5: String =
        "\uD83D\uDE1D|squinting face with tongue closed out stuck eyes\n" +
        "\uD83D\uDE1E|disappointed face\n" +
        "\uD83D\uDE1F|worried face\n" +
        "\uD83D\uDE20|angry face rag raag gussa\n" +
        "\uD83D\uDE21|enraged face rage rag raag gussa angry\n" +
        "\uD83D\uDE22|crying face cry kanna dukkho sad\n" +
        "\uD83D\uDE23|persevering face persevere\n" +
        "\uD83D\uDE24|face with steam from nose triumph\n" +
        "\uD83D\uDE25|sad but relieved face disappointed\n" +
        "\uD83D\uDE26|frowning face with open mouth\n" +
        "\uD83D\uDE27|anguished face\n" +
        "\uD83D\uDE28|fearful face\n" +
        "\uD83D\uDE29|weary face\n" +
        "\uD83D\uDE2A|sleepy face\n" +
        "\uD83D\uDE2B|tired face\n" +
        "\uD83D\uDE2C|grimacing face\n" +
        "\uD83D\uDE2D|loudly crying face sob kanna kando dukkho sad\n" +
        "\uD83D\uDE2E|face with open mouth\n" +
        "\uD83D\uDE2E\u200D\uD83D\uDCA8|face exhaling\n" +
        "\uD83D\uDE2F|hushed face\n" +
        "\uD83D\uDE30|anxious face with sweat cold\n" +
        "\uD83D\uDE31|face screaming in fear scream bhoy voy shock\n" +
        "\uD83D\uDE32|astonished face\n" +
        "\uD83D\uDE33|flushed face\n" +
        "\uD83D\uDE34|sleeping face ghum ghumano sleep\n" +
        "\uD83D\uDE35|face with crossed out eyes dizzy\n" +
        "\uD83D\uDE35\u200D\uD83D\uDCAB|face with spiral eyes\n" +
        "\uD83D\uDE36|face without mouth no\n" +
        "\uD83D\uDE36\u200D\uD83C\uDF2B\uFE0F|face in clouds\n" +
        "\uD83D\uDE37|face with medical mask\n" +
        "\uD83D\uDE38|grinning cat with smiling eyes smile\n" +
        "\uD83D\uDE39|cat with tears of joy\n" +
        "\uD83D\uDE3A|grinning cat smiley\n" +
        "\uD83D\uDE3B|smiling cat with heart eyes\n" +
        "\uD83D\uDE3C|cat with wry smile smirk\n" +
        "\uD83D\uDE3D|kissing cat\n" +
        "\uD83D\uDE3E|pouting cat\n" +
        "\uD83D\uDE3F|crying cat face\n" +
        "\uD83D\uDE40|weary cat scream\n" +
        "\uD83D\uDE41|slightly frowning face\n" +
        "\uD83D\uDE42|slightly smiling face\n" +
        "\uD83D\uDE43|upside down face\n" +
        "\uD83D\uDE44|face with rolling eyes roll\n" +
        "\uD83D\uDE45|person gesturing no good\n" +
        "\uD83D\uDE45\u200D\u2642\uFE0F|man gesturing no good\n" +
        "\uD83D\uDE45\u200D\u2640\uFE0F|woman gesturing no good\n" +
        "\uD83D\uDE46|person gesturing ok\n" +
        "\uD83D\uDE46\u200D\u2642\uFE0F|man gesturing ok\n" +
        "\uD83D\uDE46\u200D\u2640\uFE0F|woman gesturing ok\n" +
        "\uD83D\uDE47|person bowing bow\n" +
        "\uD83D\uDE47\u200D\u2642\uFE0F|man bowing\n" +
        "\uD83D\uDE47\u200D\u2640\uFE0F|woman bowing\n" +
        "\uD83D\uDE48|see no evil monkey\n" +
        "\uD83D\uDE49|hear no evil monkey\n" +
        "\uD83D\uDE4A|speak no evil monkey\n" +
        "\uD83D\uDE4B|person raising hand\n" +
        "\uD83D\uDE4B\u200D\u2642\uFE0F|man raising hand\n" +
        "\uD83D\uDE4B\u200D\u2640\uFE0F|woman raising hand\n" +
        "\uD83D\uDE4C|raising hands raised\n" +
        "\uD83D\uDE4D|person frowning\n" +
        "\uD83D\uDE4D\u200D\u2642\uFE0F|man frowning\n" +
        "\uD83D\uDE4D\u200D\u2640\uFE0F|woman frowning\n" +
        "\uD83D\uDE4E|person pouting face\n" +
        "\uD83D\uDE4E\u200D\u2642\uFE0F|man pouting\n" +
        "\uD83D\uDE4E\u200D\u2640\uFE0F|woman pouting\n" +
        "\uD83D\uDE4F|folded hands pray dua please thanks dhonnobad\n" +
        "\uD83D\uDE80|rocket\n" +
        "\uD83D\uDE81|helicopter\n" +
        "\uD83D\uDE82|locomotive steam\n" +
        "\uD83D\uDE83|railway car\n" +
        "\uD83D\uDE84|high speed train bullettrain side\n" +
        "\uD83D\uDE85|bullet train front bullettrain\n" +
        "\uD83D\uDE86|train train2\n" +
        "\uD83D\uDE87|metro\n" +
        "\uD83D\uDE88|light rail\n" +
        "\uD83D\uDE89|station\n" +
        "\uD83D\uDE8A|tram\n" +
        "\uD83D\uDE8B|tram car train\n" +
        "\uD83D\uDE8C|bus\n" +
        "\uD83D\uDE8D|oncoming bus\n" +
        "\uD83D\uDE8E|trolleybus\n" +
        "\uD83D\uDE8F|bus stop busstop\n" +
        "\uD83D\uDE90|minibus\n" +
        "\uD83D\uDE91|ambulance\n" +
        "\uD83D\uDE92|fire engine\n" +
        "\uD83D\uDE93|police car\n" +
        "\uD83D\uDE94|oncoming police car\n" +
        "\uD83D\uDE95|taxi\n" +
        "\uD83D\uDE96|oncoming taxi\n" +
        "\uD83D\uDE97|automobile car\n" +
        "\uD83D\uDE98|oncoming automobile\n" +
        "\uD83D\uDE99|sport utility vehicle blue car\n" +
        "\uD83D\uDE9A|delivery truck\n" +
        "\uD83D\uDE9B|articulated lorry\n" +
        "\uD83D\uDE9C|tractor\n" +
        "\uD83D\uDE9D|monorail\n" +
        "\uD83D\uDE9E|mountain railway\n" +
        "\uD83D\uDE9F|suspension railway\n" +
        "\uD83D\uDEA0|mountain cableway\n" +
        "\uD83D\uDEA1|aerial tramway\n" +
        "\uD83D\uDEA2|ship\n" +
        "\uD83D\uDEA3|person rowing boat rowboat\n" +
        "\uD83D\uDEA3\u200D\u2642\uFE0F|man rowing boat\n" +
        "\uD83D\uDEA3\u200D\u2640\uFE0F|woman rowing boat\n" +
        "\uD83D\uDEA4|speedboat\n" +
        "\uD83D\uDEA5|horizontal traffic light\n" +
        "\uD83D\uDEA6|vertical traffic light\n" +
        "\uD83D\uDEA7|construction\n" +
        "\uD83D\uDEA8|police car light rotating\n" +
        "\uD83D\uDEA9|triangular flag on post\n" +
        "\uD83D\uDEAA|door\n" +
        "\uD83D\uDEAB|prohibited entry no sign\n" +
        "\uD83D\uDEAC|cigarette smoking\n" +
        "\uD83D\uDEAD|no smoking\n" +
        "\uD83D\uDEAE|litter in bin sign its place put\n" +
        "\uD83D\uDEAF|no littering not do litter\n" +
        "\uD83D\uDEB0|potable water\n" +
        "\uD83D\uDEB1|non potable water non-potable\n" +
        "\uD83D\uDEB2|bicycle bike\n" +
        "\uD83D\uDEB3|no bicycles\n" +
        "\uD83D\uDEB4|person biking bicyclist\n" +
        "\uD83D\uDEB4\u200D\u2642\uFE0F|man biking\n" +
        "\uD83D\uDEB4\u200D\u2640\uFE0F|woman biking\n" +
        "\uD83D\uDEB5|person mountain biking bicyclist\n" +
        "\uD83D\uDEB5\u200D\u2642\uFE0F|man mountain biking\n" +
        "\uD83D\uDEB5\u200D\u2640\uFE0F|woman mountain biking\n" +
        "\uD83D\uDEB6|person walking\n" +
        "\uD83D\uDEB6\u200D\u2642\uFE0F|man walking\n" +
        "\uD83D\uDEB6\u200D\u2640\uFE0F|woman walking\n" +
        "\uD83D\uDEB7|no pedestrians\n" +
        "\uD83D\uDEB8|children crossing\n" +
        "\uD83D\uDEB9|men\u2019s room mens\n" +
        "\uD83D\uDEBA|women\u2019s room womens\n" +
        "\uD83D\uDEBB|restroom\n" +
        "\uD83D\uDEBC|baby symbol\n" +
        "\uD83D\uDEBD|toilet\n" +
        "\uD83D\uDEBE|water closet wc\n" +
        "\uD83D\uDEBF|shower\n" +
        "\uD83D\uDEC0|person taking bath\n" +
        "\uD83D\uDEC1|bathtub\n" +
        "\uD83D\uDEC2|passport control\n" +
        "\uD83D\uDEC3|customs\n" +
        "\uD83D\uDEC4|baggage claim\n" +
        "\uD83D\uDEC5|left luggage\n" +
        "\uD83D\uDECB\uFE0F|couch and lamp\n" +
        "\uD83D\uDECC|person in bed sleeping\n" +
        "\uD83D\uDECD\uFE0F|shopping bags\n" +
        "\uD83D\uDECE\uFE0F|bellhop bell\n" +
        "\uD83D\uDECF\uFE0F|bed\n" +
        "\uD83D\uDED0|place of worship\n" +
        "\uD83D\uDED1|stop sign\n" +
        "\uD83D\uDED2|shopping cart\n" +
        "\uD83D\uDED5|hindu temple\n" +
        "\uD83D\uDED6|hut\n" +
        "\uD83D\uDED7|elevator\n" +
        "\uD83D\uDEDC|wireless\n" +
        "\uD83D\uDEDD|playground slide\n" +
        "\uD83D\uDEDE|wheel\n" +
        "\uD83D\uDEDF|ring buoy\n" +
        "\uD83D\uDEE0\uFE0F|hammer and wrench\n" +
        "\uD83D\uDEE1\uFE0F|shield\n" +
        "\uD83D\uDEE2\uFE0F|oil drum\n" +
        "\uD83D\uDEE3\uFE0F|motorway\n" +
        "\uD83D\uDEE4\uFE0F|railway track\n" +
        "\uD83D\uDEE5\uFE0F|motor boat\n" +
        "\uD83D\uDEE9\uFE0F|small airplane\n" +
        "\uD83D\uDEEB|airplane departure flight\n" +
        "\uD83D\uDEEC|airplane arrival flight\n" +
        "\uD83D\uDEF0\uFE0F|satellite artificial\n" +
        "\uD83D\uDEF3\uFE0F|passenger ship\n" +
        "\uD83D\uDEF4|kick scooter\n" +
        "\uD83D\uDEF5|motor scooter\n" +
        "\uD83D\uDEF6|canoe\n" +
        "\uD83D\uDEF7|sled\n" +
        "\uD83D\uDEF8|flying saucer\n" +
        "\uD83D\uDEF9|skateboard\n" +
        "\uD83D\uDEFA|auto rickshaw\n" +
        "\uD83D\uDEFB|pickup truck\n" +
        "\uD83D\uDEFC|roller skate\n" +
        "\uD83D\uDFE0|orange circle\n" +
        "\uD83D\uDFE1|yellow circle\n" +
        "\uD83D\uDFE2|green circle\n" +
        "\uD83D\uDFE3|purple circle\n" +
        "\uD83D\uDFE4|brown circle\n" +
        "\uD83D\uDFE5|red square\n" +
        "\uD83D\uDFE6|blue square\n" +
        "\uD83D\uDFE7|orange square\n" +
        "\uD83D\uDFE8|yellow square\n" +
        "\uD83D\uDFE9|green square\n" +
        "\uD83D\uDFEA|purple square\n" +
        "\uD83D\uDFEB|brown square\n" +
        "\uD83D\uDFF0|heavy equals sign\n" +
        "\uD83E\uDD0C|pinched fingers\n" +
        "\uD83E\uDD0D|white heart\n" +
        "\uD83E\uDD0E|brown heart\n" +
        "\uD83E\uDD0F|pinching hand\n" +
        "\uD83E\uDD10|zipper mouth face\n" +
        "\uD83E\uDD11|money mouth face\n" +
        "\uD83E\uDD12|face with thermometer\n" +
        "\uD83E\uDD13|nerd face\n" +
        "\uD83E\uDD14|thinking face\n" +
        "\uD83E\uDD15|face with head bandage\n" +
        "\uD83E\uDD16|robot\n" +
        "\uD83E\uDD17|smiling face with open hands hugs\n" +
        "\uD83E\uDD18|sign of the horns metal\n" +
        "\uD83E\uDD19|call me hand\n" +
        "\uD83E\uDD1A|raised back of hand\n" +
        "\uD83E\uDD1B|left facing fist\n" +
        "\uD83E\uDD1C|right facing fist\n" +
        "\uD83E\uDD1D|handshake\n" +
        "\uD83E\uDD1E|crossed fingers\n" +
        "\uD83E\uDD1F|love you gesture\n" +
        "\uD83E\uDD20|cowboy hat face\n" +
        "\uD83E\uDD21|clown face\n" +
        "\uD83E\uDD22|nauseated face\n" +
        "\uD83E\uDD23|rolling on the floor laughing rofl hasi hasa lol funny\n" +
        "\uD83E\uDD24|drooling face\n" +
        "\uD83E\uDD25|lying face\n" +
        "\uD83E\uDD26|person facepalming facepalm\n" +
        "\uD83E\uDD26\u200D\u2642\uFE0F|man facepalming\n" +
        "\uD83E\uDD26\u200D\u2640\uFE0F|woman facepalming\n" +
        "\uD83E\uDD27|sneezing face\n" +
        "\uD83E\uDD28|face with raised eyebrow\n" +
        "\uD83E\uDD29|star struck\n" +
        "\uD83E\uDD2A|zany face\n" +
        "\uD83E\uDD2B|shushing face\n" +
        "\uD83E\uDD2C|face with symbols on mouth cursing\n" +
        "\uD83E\uDD2D|face with hand over mouth\n" +
        "\uD83E\uDD2E|face vomiting\n" +
        "\uD83E\uDD2F|exploding head\n" +
        "\uD83E\uDD30|pregnant woman\n" +
        "\uD83E\uDD31|breast feeding\n" +
        "\uD83E\uDD32|palms up together\n" +
        "\uD83E\uDD33|selfie\n" +
        "\uD83E\uDD34|prince\n" +
        "\uD83E\uDD35|person in tuxedo\n" +
        "\uD83E\uDD35\u200D\u2642\uFE0F|man in tuxedo\n" +
        "\uD83E\uDD35\u200D\u2640\uFE0F|woman in tuxedo\n" +
        "\uD83E\uDD36|mrs. claus mrs\n" +
        "\uD83E\uDD37|person shrugging shrug\n" +
        "\uD83E\uDD37\u200D\u2642\uFE0F|man shrugging\n" +
        "\uD83E\uDD37\u200D\u2640\uFE0F|woman shrugging\n" +
        "\uD83E\uDD38|person cartwheeling\n" +
        "\uD83E\uDD38\u200D\u2642\uFE0F|man cartwheeling\n" +
        "\uD83E\uDD38\u200D\u2640\uFE0F|woman cartwheeling\n" +
        "\uD83E\uDD39|person juggling\n" +
        "\uD83E\uDD39\u200D\u2642\uFE0F|man juggling\n" +
        "\uD83E\uDD39\u200D\u2640\uFE0F|woman juggling\n" +
        "\uD83E\uDD3A|person fencing\n" +
        "\uD83E\uDD3C|people wrestling\n" +
        "\uD83E\uDD3C\u200D\u2642\uFE0F|men wrestling\n" +
        "\uD83E\uDD3C\u200D\u2640\uFE0F|women wrestling\n" +
        "\uD83E\uDD3D|person playing water polo\n" +
        "\uD83E\uDD3D\u200D\u2642\uFE0F|man playing water polo\n" +
        "\uD83E\uDD3D\u200D\u2640\uFE0F|woman playing water polo\n" +
        "\uD83E\uDD3E|person playing handball\n" +
        "\uD83E\uDD3E\u200D\u2642\uFE0F|man playing handball\n" +
        "\uD83E\uDD3E\u200D\u2640\uFE0F|woman playing handball\n" +
        "\uD83E\uDD3F|diving mask\n" +
        "\uD83E\uDD40|wilted flower\n" +
        "\uD83E\uDD41|drum\n" +
        "\uD83E\uDD42|clinking glasses\n" +
        "\uD83E\uDD43|tumbler glass\n" +
        "\uD83E\uDD44|spoon\n"

    private val P6: String =
        "\uD83E\uDD45|goal net\n" +
        "\uD83E\uDD47|1st place medal\n" +
        "\uD83E\uDD48|2nd place medal\n" +
        "\uD83E\uDD49|3rd place medal\n" +
        "\uD83E\uDD4A|boxing glove\n" +
        "\uD83E\uDD4B|martial arts uniform\n" +
        "\uD83E\uDD4C|curling stone\n" +
        "\uD83E\uDD4D|lacrosse\n" +
        "\uD83E\uDD4E|softball\n" +
        "\uD83E\uDD4F|flying disc\n" +
        "\uD83E\uDD50|croissant\n" +
        "\uD83E\uDD51|avocado\n" +
        "\uD83E\uDD52|cucumber\n" +
        "\uD83E\uDD53|bacon\n" +
        "\uD83E\uDD54|potato\n" +
        "\uD83E\uDD55|carrot\n" +
        "\uD83E\uDD56|baguette bread\n" +
        "\uD83E\uDD57|green salad\n" +
        "\uD83E\uDD58|shallow pan of food\n" +
        "\uD83E\uDD59|stuffed flatbread\n" +
        "\uD83E\uDD5A|egg\n" +
        "\uD83E\uDD5B|glass of milk\n" +
        "\uD83E\uDD5C|peanuts\n" +
        "\uD83E\uDD5D|kiwi fruit\n" +
        "\uD83E\uDD5E|pancakes\n" +
        "\uD83E\uDD5F|dumpling\n" +
        "\uD83E\uDD60|fortune cookie\n" +
        "\uD83E\uDD61|takeout box\n" +
        "\uD83E\uDD62|chopsticks\n" +
        "\uD83E\uDD63|bowl with spoon\n" +
        "\uD83E\uDD64|cup with straw\n" +
        "\uD83E\uDD65|coconut\n" +
        "\uD83E\uDD66|broccoli\n" +
        "\uD83E\uDD67|pie\n" +
        "\uD83E\uDD68|pretzel\n" +
        "\uD83E\uDD69|cut of meat\n" +
        "\uD83E\uDD6A|sandwich\n" +
        "\uD83E\uDD6B|canned food\n" +
        "\uD83E\uDD6C|leafy green\n" +
        "\uD83E\uDD6D|mango\n" +
        "\uD83E\uDD6E|moon cake\n" +
        "\uD83E\uDD6F|bagel\n" +
        "\uD83E\uDD70|smiling face with hearts three\n" +
        "\uD83E\uDD71|yawning face\n" +
        "\uD83E\uDD72|smiling face with tear\n" +
        "\uD83E\uDD73|partying face party utsob celebrate\n" +
        "\uD83E\uDD74|woozy face\n" +
        "\uD83E\uDD75|hot face\n" +
        "\uD83E\uDD76|cold face\n" +
        "\uD83E\uDD77|ninja\n" +
        "\uD83E\uDD78|disguised face\n" +
        "\uD83E\uDD79|face holding back tears\n" +
        "\uD83E\uDD7A|pleading face\n" +
        "\uD83E\uDD7B|sari\n" +
        "\uD83E\uDD7C|lab coat\n" +
        "\uD83E\uDD7D|goggles\n" +
        "\uD83E\uDD7E|hiking boot\n" +
        "\uD83E\uDD7F|flat shoe\n" +
        "\uD83E\uDD80|crab\n" +
        "\uD83E\uDD81|lion\n" +
        "\uD83E\uDD82|scorpion\n" +
        "\uD83E\uDD83|turkey\n" +
        "\uD83E\uDD84|unicorn\n" +
        "\uD83E\uDD85|eagle\n" +
        "\uD83E\uDD86|duck\n" +
        "\uD83E\uDD87|bat\n" +
        "\uD83E\uDD88|shark\n" +
        "\uD83E\uDD89|owl\n" +
        "\uD83E\uDD8A|fox face\n" +
        "\uD83E\uDD8B|butterfly\n" +
        "\uD83E\uDD8C|deer\n" +
        "\uD83E\uDD8D|gorilla\n" +
        "\uD83E\uDD8E|lizard\n" +
        "\uD83E\uDD8F|rhinoceros\n" +
        "\uD83E\uDD90|shrimp\n" +
        "\uD83E\uDD91|squid\n" +
        "\uD83E\uDD92|giraffe\n" +
        "\uD83E\uDD93|zebra\n" +
        "\uD83E\uDD94|hedgehog\n" +
        "\uD83E\uDD95|sauropod\n" +
        "\uD83E\uDD96|t rex t-rex\n" +
        "\uD83E\uDD97|cricket\n" +
        "\uD83E\uDD98|kangaroo\n" +
        "\uD83E\uDD99|llama\n" +
        "\uD83E\uDD9A|peacock\n" +
        "\uD83E\uDD9B|hippopotamus\n" +
        "\uD83E\uDD9C|parrot\n" +
        "\uD83E\uDD9D|raccoon\n" +
        "\uD83E\uDD9E|lobster\n" +
        "\uD83E\uDD9F|mosquito\n" +
        "\uD83E\uDDA0|microbe\n" +
        "\uD83E\uDDA1|badger\n" +
        "\uD83E\uDDA2|swan\n" +
        "\uD83E\uDDA3|mammoth\n" +
        "\uD83E\uDDA4|dodo\n" +
        "\uD83E\uDDA5|sloth\n" +
        "\uD83E\uDDA6|otter\n" +
        "\uD83E\uDDA7|orangutan\n" +
        "\uD83E\uDDA8|skunk\n" +
        "\uD83E\uDDA9|flamingo\n" +
        "\uD83E\uDDAA|oyster\n" +
        "\uD83E\uDDAB|beaver\n" +
        "\uD83E\uDDAC|bison\n" +
        "\uD83E\uDDAD|seal\n" +
        "\uD83E\uDDAE|guide dog\n" +
        "\uD83E\uDDAF|white cane probing\n" +
        "\uD83E\uDDB4|bone\n" +
        "\uD83E\uDDB5|leg\n" +
        "\uD83E\uDDB6|foot\n" +
        "\uD83E\uDDB7|tooth\n" +
        "\uD83E\uDDB8|superhero\n" +
        "\uD83E\uDDB8\u200D\u2642\uFE0F|man superhero\n" +
        "\uD83E\uDDB8\u200D\u2640\uFE0F|woman superhero\n" +
        "\uD83E\uDDB9|supervillain\n" +
        "\uD83E\uDDB9\u200D\u2642\uFE0F|man supervillain\n" +
        "\uD83E\uDDB9\u200D\u2640\uFE0F|woman supervillain\n" +
        "\uD83E\uDDBA|safety vest\n" +
        "\uD83E\uDDBB|ear with hearing aid\n" +
        "\uD83E\uDDBC|motorized wheelchair\n" +
        "\uD83E\uDDBD|manual wheelchair\n" +
        "\uD83E\uDDBE|mechanical arm\n" +
        "\uD83E\uDDBF|mechanical leg\n" +
        "\uD83E\uDDC0|cheese wedge\n" +
        "\uD83E\uDDC1|cupcake\n" +
        "\uD83E\uDDC2|salt\n" +
        "\uD83E\uDDC3|beverage box\n" +
        "\uD83E\uDDC4|garlic\n" +
        "\uD83E\uDDC5|onion\n" +
        "\uD83E\uDDC6|falafel\n" +
        "\uD83E\uDDC7|waffle\n" +
        "\uD83E\uDDC8|butter\n" +
        "\uD83E\uDDC9|mate\n" +
        "\uD83E\uDDCA|ice cube\n" +
        "\uD83E\uDDCB|bubble tea\n" +
        "\uD83E\uDDCC|troll\n" +
        "\uD83E\uDDCD|person standing\n" +
        "\uD83E\uDDCD\u200D\u2642\uFE0F|man standing\n" +
        "\uD83E\uDDCD\u200D\u2640\uFE0F|woman standing\n" +
        "\uD83E\uDDCE|person kneeling\n" +
        "\uD83E\uDDCE\u200D\u2642\uFE0F|man kneeling\n" +
        "\uD83E\uDDCE\u200D\u2640\uFE0F|woman kneeling\n" +
        "\uD83E\uDDCF|deaf person\n" +
        "\uD83E\uDDCF\u200D\u2642\uFE0F|deaf man\n" +
        "\uD83E\uDDCF\u200D\u2640\uFE0F|deaf woman\n" +
        "\uD83E\uDDD0|face with monocle\n" +
        "\uD83E\uDDD1|person adult\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDFA8|artist\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDE80|astronaut\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDF73|cook\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDFED|factory worker\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDF3E|farmer\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDE92|firefighter\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDD27|mechanic\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDF84|mx claus\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDCBC|office worker\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDB2|person bald\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDB1|person curly hair\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDF7C|person feeding baby\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDBD|person in manual wheelchair\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDBC|person in motorized wheelchair\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDB0|person red hair\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDB3|person white hair\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDDAF|person with white cane probing\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDD2C|scientist\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDFA4|singer\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDF93|student\n" +
        "\uD83E\uDDD1\u200D\uD83C\uDFEB|teacher\n" +
        "\uD83E\uDDD1\u200D\uD83D\uDCBB|technologist\n" +
        "\uD83E\uDDD1\u200D\u2695\uFE0F|health worker\n" +
        "\uD83E\uDDD1\u200D\u2696\uFE0F|judge\n" +
        "\uD83E\uDDD1\u200D\u2708\uFE0F|pilot\n" +
        "\uD83E\uDDD1\u200D\uD83E\uDD1D\u200D\uD83E\uDDD1|people holding hands\n" +
        "\uD83E\uDDD2|child\n" +
        "\uD83E\uDDD3|older person adult\n" +
        "\uD83E\uDDD4|person beard bearded\n" +
        "\uD83E\uDDD4\u200D\u2642\uFE0F|man beard\n" +
        "\uD83E\uDDD4\u200D\u2640\uFE0F|woman beard\n" +
        "\uD83E\uDDD5|woman with headscarf\n" +
        "\uD83E\uDDD6|person in steamy room sauna\n" +
        "\uD83E\uDDD6\u200D\u2642\uFE0F|man in steamy room sauna\n" +
        "\uD83E\uDDD6\u200D\u2640\uFE0F|woman in steamy room sauna\n" +
        "\uD83E\uDDD7|person climbing\n" +
        "\uD83E\uDDD7\u200D\u2642\uFE0F|man climbing\n" +
        "\uD83E\uDDD7\u200D\u2640\uFE0F|woman climbing\n" +
        "\uD83E\uDDD8|person in lotus position\n" +
        "\uD83E\uDDD8\u200D\u2642\uFE0F|man in lotus position\n" +
        "\uD83E\uDDD8\u200D\u2640\uFE0F|woman in lotus position\n" +
        "\uD83E\uDDD9|mage\n" +
        "\uD83E\uDDD9\u200D\u2642\uFE0F|man mage\n" +
        "\uD83E\uDDD9\u200D\u2640\uFE0F|woman mage\n" +
        "\uD83E\uDDDA|fairy\n" +
        "\uD83E\uDDDA\u200D\u2642\uFE0F|man fairy\n" +
        "\uD83E\uDDDA\u200D\u2640\uFE0F|woman fairy\n" +
        "\uD83E\uDDDB|vampire\n" +
        "\uD83E\uDDDB\u200D\u2642\uFE0F|man vampire\n" +
        "\uD83E\uDDDB\u200D\u2640\uFE0F|woman vampire\n" +
        "\uD83E\uDDDC|merperson\n" +
        "\uD83E\uDDDC\u200D\u2640\uFE0F|mermaid\n" +
        "\uD83E\uDDDC\u200D\u2642\uFE0F|merman\n" +
        "\uD83E\uDDDD|elf\n" +
        "\uD83E\uDDDD\u200D\u2642\uFE0F|man elf\n" +
        "\uD83E\uDDDD\u200D\u2640\uFE0F|woman elf\n" +
        "\uD83E\uDDDE|genie\n" +
        "\uD83E\uDDDE\u200D\u2642\uFE0F|man genie\n" +
        "\uD83E\uDDDE\u200D\u2640\uFE0F|woman genie\n" +
        "\uD83E\uDDDF|zombie\n" +
        "\uD83E\uDDDF\u200D\u2642\uFE0F|man zombie\n" +
        "\uD83E\uDDDF\u200D\u2640\uFE0F|woman zombie\n" +
        "\uD83E\uDDE0|brain\n" +
        "\uD83E\uDDE1|orange heart\n" +
        "\uD83E\uDDE2|billed cap\n" +
        "\uD83E\uDDE3|scarf\n" +
        "\uD83E\uDDE4|gloves\n" +
        "\uD83E\uDDE5|coat\n" +
        "\uD83E\uDDE6|socks\n" +
        "\uD83E\uDDE7|red envelope\n" +
        "\uD83E\uDDE8|firecracker\n" +
        "\uD83E\uDDE9|puzzle piece jigsaw\n" +
        "\uD83E\uDDEA|test tube\n" +
        "\uD83E\uDDEB|petri dish\n" +
        "\uD83E\uDDEC|dna\n" +
        "\uD83E\uDDED|compass\n" +
        "\uD83E\uDDEE|abacus\n" +
        "\uD83E\uDDEF|fire extinguisher\n" +
        "\uD83E\uDDF0|toolbox\n" +
        "\uD83E\uDDF1|brick bricks\n" +
        "\uD83E\uDDF2|magnet\n" +
        "\uD83E\uDDF3|luggage\n" +
        "\uD83E\uDDF4|lotion bottle\n" +
        "\uD83E\uDDF5|thread\n" +
        "\uD83E\uDDF6|yarn\n" +
        "\uD83E\uDDF7|safety pin\n" +
        "\uD83E\uDDF8|teddy bear\n" +
        "\uD83E\uDDF9|broom\n" +
        "\uD83E\uDDFA|basket\n" +
        "\uD83E\uDDFB|roll of paper\n" +
        "\uD83E\uDDFC|soap\n" +
        "\uD83E\uDDFD|sponge\n" +
        "\uD83E\uDDFE|receipt\n" +
        "\uD83E\uDDFF|nazar amulet\n" +
        "\uD83E\uDE70|ballet shoes\n" +
        "\uD83E\uDE71|one piece swimsuit\n" +
        "\uD83E\uDE72|briefs brief swim\n" +
        "\uD83E\uDE73|shorts\n" +
        "\uD83E\uDE74|thong sandal\n" +
        "\uD83E\uDE75|light blue heart\n" +
        "\uD83E\uDE76|grey heart\n" +
        "\uD83E\uDE77|pink heart\n" +
        "\uD83E\uDE78|drop of blood\n" +
        "\uD83E\uDE79|adhesive bandage\n" +
        "\uD83E\uDE7A|stethoscope\n" +
        "\uD83E\uDE7B|x ray\n" +
        "\uD83E\uDE7C|crutch\n" +
        "\uD83E\uDE80|yo yo\n" +
        "\uD83E\uDE81|kite\n" +
        "\uD83E\uDE82|parachute\n" +
        "\uD83E\uDE83|boomerang\n" +
        "\uD83E\uDE84|magic wand\n" +
        "\uD83E\uDE85|pi\u00F1ata pinata\n" +
        "\uD83E\uDE86|nesting dolls\n" +
        "\uD83E\uDE87|maracas\n" +
        "\uD83E\uDE88|flute\n" +
        "\uD83E\uDE90|ringed planet\n" +
        "\uD83E\uDE91|chair\n" +
        "\uD83E\uDE92|razor\n" +
        "\uD83E\uDE93|axe\n" +
        "\uD83E\uDE94|diya lamp\n" +
        "\uD83E\uDE95|banjo\n" +
        "\uD83E\uDE96|military helmet\n" +
        "\uD83E\uDE97|accordion\n" +
        "\uD83E\uDE98|long drum\n" +
        "\uD83E\uDE99|coin\n" +
        "\uD83E\uDE9A|carpentry saw\n" +
        "\uD83E\uDE9B|screwdriver\n" +
        "\uD83E\uDE9C|ladder\n" +
        "\uD83E\uDE9D|hook\n" +
        "\uD83E\uDE9E|mirror\n" +
        "\uD83E\uDE9F|window\n" +
        "\uD83E\uDEA0|plunger\n" +
        "\uD83E\uDEA1|sewing needle\n" +
        "\uD83E\uDEA2|knot\n" +
        "\uD83E\uDEA3|bucket\n" +
        "\uD83E\uDEA4|mouse trap\n" +
        "\uD83E\uDEA5|toothbrush\n" +
        "\uD83E\uDEA6|headstone\n" +
        "\uD83E\uDEA7|placard\n" +
        "\uD83E\uDEA8|rock\n" +
        "\uD83E\uDEA9|mirror ball\n" +
        "\uD83E\uDEAA|identification card\n" +
        "\uD83E\uDEAB|low battery\n" +
        "\uD83E\uDEAC|hamsa\n" +
        "\uD83E\uDEAD|folding hand fan\n" +
        "\uD83E\uDEAE|hair pick\n" +
        "\uD83E\uDEAF|khanda\n" +
        "\uD83E\uDEB0|fly\n" +
        "\uD83E\uDEB1|worm\n" +
        "\uD83E\uDEB2|beetle\n" +
        "\uD83E\uDEB3|cockroach\n" +
        "\uD83E\uDEB4|potted plant\n" +
        "\uD83E\uDEB5|wood\n" +
        "\uD83E\uDEB6|feather\n" +
        "\uD83E\uDEB7|lotus\n" +
        "\uD83E\uDEB8|coral\n" +
        "\uD83E\uDEB9|empty nest\n" +
        "\uD83E\uDEBA|nest with eggs\n" +
        "\uD83E\uDEBB|hyacinth\n" +
        "\uD83E\uDEBC|jellyfish\n" +
        "\uD83E\uDEBD|wing\n" +
        "\uD83E\uDEBF|goose\n" +
        "\uD83E\uDEC0|anatomical heart\n" +
        "\uD83E\uDEC1|lungs\n" +
        "\uD83E\uDEC2|people hugging\n" +
        "\uD83E\uDEC3|pregnant man\n" +
        "\uD83E\uDEC4|pregnant person\n" +
        "\uD83E\uDEC5|person with crown\n" +
        "\uD83E\uDECE|moose\n" +
        "\uD83E\uDECF|donkey\n" +
        "\uD83E\uDED0|blueberries\n" +
        "\uD83E\uDED1|bell pepper\n" +
        "\uD83E\uDED2|olive\n"

    private val P7: String =
        "\uD83E\uDED3|flatbread\n" +
        "\uD83E\uDED4|tamale\n" +
        "\uD83E\uDED5|fondue\n" +
        "\uD83E\uDED6|teapot\n" +
        "\uD83E\uDED7|pouring liquid\n" +
        "\uD83E\uDED8|beans\n" +
        "\uD83E\uDED9|jar\n" +
        "\uD83E\uDEDA|ginger root\n" +
        "\uD83E\uDEDB|pea pod\n" +
        "\uD83E\uDEE0|melting face\n" +
        "\uD83E\uDEE1|saluting face\n" +
        "\uD83E\uDEE2|face with open eyes and hand over mouth\n" +
        "\uD83E\uDEE3|face with peeking eye\n" +
        "\uD83E\uDEE4|face with diagonal mouth\n" +
        "\uD83E\uDEE5|dotted line face\n" +
        "\uD83E\uDEE6|biting lip\n" +
        "\uD83E\uDEE7|bubbles\n" +
        "\uD83E\uDEE8|shaking face\n" +
        "\uD83E\uDEF0|hand with index finger and thumb crossed\n" +
        "\uD83E\uDEF1|rightwards hand\n" +
        "\uD83E\uDEF2|leftwards hand\n" +
        "\uD83E\uDEF3|palm down hand\n" +
        "\uD83E\uDEF4|palm up hand\n" +
        "\uD83E\uDEF5|index pointing at the viewer\n" +
        "\uD83E\uDEF6|heart hands\n" +
        "\uD83E\uDEF7|leftwards pushing hand\n" +
        "\uD83E\uDEF8|rightwards pushing hand\n"

    private val entries: List<Entry> by lazy {
        val res = ArrayList<Entry>()
        for (chunk in listOf(P0, P1, P2, P3, P4, P5, P6, P7)) {
            for (line in chunk.split('\n')) {
                val i = line.indexOf('|')
                if (i <= 0) continue
                res.add(Entry(line.substring(0, i), " " + line.substring(i + 1) + " "))
            }
        }
        res
    }

    /** [query] (এক বা একাধিক শব্দ) অনুযায়ী ইমোজি খোঁজে। শব্দের শুরু মিললে আগে আসে। */
    fun search(query: String, limit: Int = 60): List<String> {
        val toks = query.trim().lowercase().split(' ').filter { it.isNotEmpty() }
        if (toks.isEmpty()) return emptyList()
        val scored = ArrayList<Pair<Int, String>>()
        for (e in entries) {
            var total = 0
            var ok = true
            for (t in toks) {
                if (e.words.contains(" " + t)) {
                    total += 2
                    if (e.words.startsWith(" " + t + " ")) total += 1
                } else if (e.words.contains(t)) {
                    total += 1
                } else {
                    ok = false
                    break
                }
            }
            if (ok) scored.add(Pair(total, e.emoji))
        }
        return scored
            .sortedByDescending { it.first }
            .map { it.second }
            .filter { EmojiData.supported(it) }
            .take(limit)
    }
}
