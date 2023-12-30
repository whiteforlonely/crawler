package com.ake.ckey.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LanguageEnum {


    ZH_CN("zh-CN", "中文简体","汉语(简体)", "Chinese(Simple)"),
    ZH_HK("zh-HK", "中文繁体","汉语(繁体)", "Chinese(Traditional)"),
    ZH_TW("zh-TW", "中文粤语","汉语(粤语)", "Chinese(HongKong)"),
    EN_US("en-US", "English","英语", "English"),
    AR("ar", "العربية","阿拉伯语", "Arabic"),
    PT("pt", "Português","葡萄牙语", "Portuguese"),
    RU("ru", "Русский","俄语", "Russian"),
    ES("es", "Español","西班牙语", "Spanish"),
    HI("hi", "हिन्दी","印地语", "Hindi"),
    JA("ja", "日本語","日语", "Japanese"),
    TR("tr", "Türkçe","土耳其语", "Turkish"),
    KO("ko", "한국어", "韩语", "Korean "),
    FR("fr", "Français","法语", "French"),
    DE("de", "Deutsch","德语", "German"),
    VI("vi", "Tiếng Việt","越南语", "Vietnamese"),
    UR("ur", "اردو","乌尔都语", "Urdu"),
    JW("jw", "ꦧꦱꦗꦮ","爪哇语", "Javanese"),
    IT("it", "Italiano","意大利语", "Italian"),
    BN("bn", "বাংলা","孟加拉语", "Bengali"),
   /* FA("fa", "فارسی","波斯语", "Persian"),
    HA("ha", "Hausa","豪萨语", "Hausa"),
    MS("ms", "Marais","马来语", "Malay"),
    ID("id", "Bahasa Indonesia","印度尼西亚语", "Indonesian"),
    PL("pl", "Polski","波兰语", "Polish"),
    NL("nl", "Nederlands","荷兰语", "Dutch"),
    MY("my", "မြန်မာစာ","缅甸语", "Burmese"),
    UK("uk", "Українська","乌克兰语", "Ukrainian"),
    TH("th", "ไทย","泰语", "Thai"),
    EL("el", "Ελληνικά","希腊语", "Greek"),
    UZ("uz", "Oʻzbek tili","乌兹别克语", "Uzbek"),
    RO("ro", "Română","罗马尼亚语", "Romanian"),
    KK("kk", "Қазақ тілі","哈萨克语", "Kazakh"),
    HU("hu", "Magyar","匈牙利语", "Hungarian"),
    CS("cs", "Čeština","捷克语", "Czech"),
    KM("km", "ភាសាខ្មែរ","高棉语", "Khmer"),
    TL("tl", "Tagalog","塔加洛语", "Tagalog"),
    LA("la", "Latinus","拉丁语", "Latin"),
    ZU("zu", "IsiZulu","祖鲁语", "Zulu"),
    NE("ne", "नेपाली","尼泊尔语", "Nepali"),
    PA("pa", "ਪੰਜਾਬੀ","旁遮普语", "Punjabi"),
    TE("te", "తెలుగు","泰卢固语", "Telugu"),
    MR("mr", "मराठी","马拉塔语", "Marathi"),
    TA("ta", "தமிழ்","泰米尔语", "Tamil"),
    PS("ps", "پښتو","普什图语", "Pashto"),
    GU("gu", "ગુજરાતી","古吉拉特语", "Gujarati"),
    KN("kn", "ಕನ್ನಡ","卡纳达语", "Kannada"),
    ML("ml", "മലയാളം","马拉雅拉姆语", "Malayalam"),
    SU("su", "Basa Sunda","巽他语", "Sundanese"),
    OR("or", "ଓଡ଼ିଆ","奥里亚语", "Oriya"),
    BHO("bho", "भोजपुरी","博杰普尔语", "Bhojpuri"),
    YO("yo", "Yorùbá","约鲁巴语", "Yoruba"),
    MAI("mai", "मैथिली","迈蒂利语", "Maithili"),
    SD("sd", "سنڌي","信德语", "Sindhi"),
    AM("am", "አማርኛ","阿姆哈拉语", "Amharic"),
    OM("om", "Afaan Oromoo","奥罗莫语", "Oromo"),
    IG("ig", "Igbo","伊博语", "Igbo"),
//    UK("uk", "अवधी","阿瓦德语", "Awadhi"),
    AZ("az", "Azərbaycan dili","阿塞拜疆语", "Azerbaijani"),
    CEB("ceb", "Cebuano","宿务语", "Cebuano"),
    KU("ku", "Kurdî","库尔德语", "Kurdish"),
    SR("sr", "Srpskohrvatski/Српскохрватски","塞尔维亚语-克罗地亚语", "Serbo-Croatian"),
//    UK("uk", "سرائیکی","萨拉基语", "Saraiki"),
    SO("so", "Soomaaliga","索马里语", "Somali"),
//    KM("km", "Kinyarwanda","基尼亚卢旺达语", "Kinyarwanda"),
//    KM("km", "chiShona","绍纳语", "Shona"),
//    KM("km", "छत्तीसगढ़ी","恰蒂斯加尔希语", "Chhattisgarhi"),
//    KM("km", "देवनागरी","德干语", "Deccan"),
//    KM("km", "Akan","阿寒语", "Akan"),
//    KM("km", "Sylheti","锡尔赫提语", "Sylheti"),
    SV("sv", "Svenska","瑞典语", "Swedish"),
//    KM("km", "मारवाड़ी","马尔瓦里语", "Marwari"),
//    KM("km", "मगही","马加希语", "Magahi"),
    UG("ug", "ئۇيغۇرچە","维吾尔语", "Uyghur"),
//    KM("km", "ढूंढ़डी","敦达里语", "Dhundari"),
    HT("ht", "Kreyòl ayisyen","海地克里奥尔语", "Haitian Creole"),
    ILO("ilo", "Ilocano","洛卡诺语", "llocano"),
//    KM("km", "Runa Simi","盖丘亚语", "Quechua"),
//    KM("km", "Ποντιακά","希腊庞蒂克语", "Greek Pontic"),
    BE("be", "Беларуская","白俄罗斯语", "Belarusian"),
//    KM("km", "Langaj","海地伏都教语言", "Haitian Vodou"),
//    KM("km", "Kikongo","基孔戈语", "Kikongo"),
//    KM("km", "isiXhosa","科萨语", "Xhosa"),
//    KM("km", "Mooré","莫西语", "Mossi"),
//    KM("km", "कोंकणी","孔卡尼语", "Konkani"),
//    KM("km", "کٲشُر","克什米尔语", "Kashmiri"),
//    KM("km", "Հայերեն","亚美尼亚语", "Armenian"),
    TT("tt", "Татар теле","鞑靼语", "Tatar"),
    LO("lo", "ພາສາລາວ","老挝语", "Lao"),
    SI("si", "සිංහල","僧伽罗语", "Sinhala"),
    DV("dv", " ދިވެހި ","迪维希语", "Dhivehi"),
//    KM("km", "Авар мацӀ","阿瓦尔语", "Avar"),
//    KM("km", "بلوچی","俾路支语", "Balochi"),
//    KM("km", "chiTumbuka","通布卡语", "Tumbuka"),
//    KM("km", "Адыгэбзэ","阿迪格语", "Adyghe"),
//    KM("km", "Alsacien","阿尔萨斯语", "Alsatian"),*/
    ;

    private final String languageCode;

    private final String desc;
    private final String chineseDesc;
    private final String englishDesc;


    LanguageEnum(String languageCode, String desc, String chineseDesc, String englishDesc) {
        this.languageCode = languageCode;
        this.desc = desc;
        this.chineseDesc =chineseDesc;
        this.englishDesc = englishDesc;
    }

    public static final Map<String, LanguageEnum> codeMap;
    public static final Map<String, LanguageEnum> descMap;

    static {
        codeMap = Arrays.stream(values()).collect(Collectors.toMap(LanguageEnum::getLanguageCode, Function.identity()));
        descMap = Arrays.stream(values()).collect(Collectors.toMap(LanguageEnum::getChineseDesc, Function.identity()));
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getDesc() {
        return desc;
    }

    public String getChineseDesc() {
        return chineseDesc;
    }

    public String getEnglishDesc() {
        return englishDesc;
    }
}
