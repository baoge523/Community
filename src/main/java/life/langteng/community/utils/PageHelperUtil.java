package life.langteng.community.utils;

public class PageHelperUtil {

    /**
     * 保证currentPage总是合法
     * @param currentPage
     * @param pageSize
     * @param total
     * @return
     */
    public static Integer validCurrentPage(Integer currentPage,Integer pageSize,Integer total){
        /**
         * 容错  最小值
         */
        if (currentPage < 1) {
            currentPage = 1;
        }

        // 总页数
        int totalPages;

        // 总记录条数 为0 时
        if (total == 0){
            totalPages = 1;  // 默认是第一页，避免 currentPage(1) > totalPages(0)  --> currentPage = totalPages = 0 的问题
        }else{
            totalPages = ((total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1));
        }

        /**
         * 容错 最大值
         */
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        return currentPage;
    }
}
