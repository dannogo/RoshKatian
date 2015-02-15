package player.com.roshkatian;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дом on 09.01.2015.
 */
public class HtmlHelper {
    TagNode rootNode;

    //Конструктор
    public HtmlHelper(URL htmlPage) throws IOException {
        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(htmlPage);
    }

    List<TagNode> getLinksByUl() {
        List<TagNode> liList = new ArrayList<TagNode>();
        TagNode linkElementsGenre[] = rootNode.getElementsByName("ul", true);
        for (int i = 0; linkElementsGenre != null && i < linkElementsGenre.length; i++)
        {
            String className = linkElementsGenre[i].getAttributeByName("class");
            if (className.equals("big_main")) {
                TagNode li[] = linkElementsGenre[i].getElementsByName("a", true);
                for (int j = 0; li != null && j < li.length; j++) {

                    liList.add(li[j]);

                }

            }
        }
    return liList;
    }

    List<TagNode> getLinksByClass(String CSSClassname)
    {

        List<TagNode> linkList = new ArrayList<TagNode>();

            //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++) {

                //получаем атрибут по имени
                String classType = linkElements[i].getAttributeByName("class");
                //если атрибут есть и он эквивалентен искомому, то добавляем в список

                if (classType != null && classType.equals(CSSClassname)) {
                    linkList.add(linkElements[i]);
                }
            }

        return linkList;
    }
}
