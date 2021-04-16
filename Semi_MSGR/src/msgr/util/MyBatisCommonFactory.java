package msgr.util;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisCommonFactory {
	private static SqlSessionFactory sqlSessionFactory = null;
//	private SqlSession sqlSession = null;

	public static SqlSessionFactory getInstance() {

		if (sqlSessionFactory == null) {

			synchronized (SqlSessionFactory.class) {

				// 동기화 블럭 들어온 후에도 null 체크
				if (sqlSessionFactory == null) {

					try {
						String	resource	= "mybatis-config.xml";
						Reader	reader		= Resources.getResourceAsReader(resource);
						sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			} // synchronization
		} // end of if
		return sqlSessionFactory;
	}// end of getInstance()
}
