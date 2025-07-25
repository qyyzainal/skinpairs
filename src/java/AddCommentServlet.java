import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddCommentServlet")
public class AddCommentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        int postId = Integer.parseInt(request.getParameter("postId"));
        String commentText = request.getParameter("comment_text");

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement logStmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/skinpairs_db", "qyy", "");

            // Insert comment into database
            String sql = "INSERT INTO post_comments (post_id, username, comment_text, commented_at) VALUES (?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            stmt.setString(2, username);
            stmt.setString(3, commentText);
            stmt.executeUpdate();
            stmt.close();

            // Insert into activity log
            String logSql = "INSERT INTO activity_log (activity_type, description) VALUES (?, ?)";
            logStmt = conn.prepareStatement(logSql);
            logStmt.setString(1, "comment");
            logStmt.setString(2, "User '" + username + "' commented on post ID " + postId);
            logStmt.executeUpdate();
            logStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (logStmt != null) logStmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        response.sendRedirect("CommunityPost_User.jsp");
    }
}
