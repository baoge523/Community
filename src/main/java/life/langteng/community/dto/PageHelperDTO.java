package life.langteng.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 */
@Data
public class  PageHelperDTO<T> {

    private static final Integer FIRST_PAGE = 1;

    /**
     * 如果页数足够多的情况下，最多显示 7 页
     */
    private static final Integer MAX_SHOW_NUMBER = 7;

    /**
     * 该页需要展示的数据
     */
    private List<T> datas;

    /**
     * 所有展示的页数 如: 1234567  2345678 ...
     */
    private List<Integer> pageNumbers = new ArrayList<>();

    /**
     * 是否展示上一页按钮  <
     */
    private boolean previous;
    /**
     * 是否展示首页按钮
     */
    private boolean firstPage;
    /**
     * 是否展示下一页按钮
     */
    private boolean next;
    /**
     * 是否展示最后一页按钮
     */
    private boolean lastPage;
    /**
     * 当前页的页数
     */
    private Integer currentPage;
    /**
     * 一共有多少页
     */
    private Integer pageCount;
    /**
     * 首页
     */
    private Integer homePage = FIRST_PAGE;



    /**
     * @param datas   该页需要展示的数据
     * @param currentPage 当前页
     * @param pageSize    每页最多有多少条数据
     * @param total       一共有多少条数据
     */
    public PageHelperDTO(List<T> datas,Integer currentPage,Integer pageSize,Integer total){

        /**
         * 这里暂时不校验数据
         */
        this.datas = datas;
        this.currentPage = currentPage;
        this.pageCount = ((total % pageSize == 0) ? (total / pageSize) : (total / pageSize +1));

        /**
         *
         * 确定 pageNumbers 中的数据
         *
         *  如果页数足够多的情况下，最多显示 7 页
         *
         *  current-3  current-2  current-1  current  current+1  current+2  current+3
         */

        /**
         * 总页数小于等于 7时
         */
        if (this.pageCount>=1 && this.pageCount <= MAX_SHOW_NUMBER) {
            for (int i =1;i<=this.pageCount;i++){
                this.pageNumbers.add(i);
            }
        }else{
            /**
             *    1   1234567    current-3<=0?1 :current-3    确定最小页数
             *    2   1234567
             *    3   1234567
             *    4   1234567
             *    5   2345678
             *    6   3456789
             *    7   45678910
             *    8   45678910
             *    9   45678910
             *    10  45678910     current +3 >= last? last : current+3   确定最大页数
             */

            // 确定最小页
            int minPage = this.currentPage - 3 <= 0 ? 1 : this.currentPage - 3;
            // 确定最大页
            int maxPage = this.currentPage + 3 >= this.pageCount ? this.pageCount : this.currentPage + 3;

            for(int i=minPage;i<=maxPage;i++){
                this.pageNumbers.add(i);
            }
        }

        /**
         * 只有当 当前页为第一页的时候，previous 不展示
         */
        if(this.currentPage == 1){
            this.previous = false;
        }else{
            this.previous = true;
        }

        /**
         * 当展示的页数集合中
         *
         *             包含第一页的时候，不展示 首页
         *
         *             不包含的第一页的时候，就展示  首页
         */
        if(pageNumbers.contains(FIRST_PAGE)){
            this.firstPage = false;
        }else{
            this.firstPage = true;
        }

        /**
         * 如果当前页是最后页面，那么就没有下一页
         */
        if(this.currentPage == this.pageCount){
            this.next = false;
        }else{
            this.next = true;
        }

        /**
         * 如果页数集合中
         *           包含最后一页,就不显示    末页
         *           不包含最后一页，就显示   末页
         *
         */
        if(this.pageNumbers.contains(this.pageCount)){
            this.lastPage = false;
        }else{
            this.lastPage = true;
        }
    }

}
