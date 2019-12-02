package olympic;

import olympic.business.Athlete;
import olympic.business.ReturnValue;
import olympic.business.Sport;
import olympic.data.DBConnector;
import olympic.data.PostgreSQLErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static olympic.business.ReturnValue.*;


public class Solution {
    public static void main(String[] args) {
        dropTables();
        createTables();
        Athlete athlete= new Athlete();
        athlete.setId(8);
        athlete.setCountry("Israel");
        athlete.setName("moshe");
        athlete.setIsActive(true);
        ReturnValue r= addAthlete(athlete);
        System.out.println(r);
        Athlete rathlete= getAthleteProfile(8);
        System.out.println(rathlete.toString());
        r= deleteAthlete(athlete);
        System.out.println(r);
        //dropTables();
    }

    public static void createTables() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = connection.prepareStatement("CREATE TABLE Athlete\n" +
                    "(\n" +
                    "    Athlete_ID integer,\n" +
                    "    Athlete_name text not null ,\n" +
                    "    Country text not null,\n"+
                    "    Active boolean not null,\n"+
                    "    PRIMARY KEY (Athlete_ID),\n" +
                    "    CHECK (Athlete_ID > 0)\n" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }

        try {

            pstmt = connection.prepareStatement("CREATE TABLE Sport\n" +
                    "(\n" +
                    "    Sport_ID integer,\n" +
                    "    Sport_name text not null ,\n" +
                    "    City text not null,\n"+
                    "    Athletes_Counter integer default (0),\n"+
                    "    PRIMARY KEY (Sport_ID),\n" +
                    "    CHECK (Sport_ID > 0)\n" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }

        try {
            pstmt = connection.prepareStatement("CREATE TABLE AthleteStatus\n" +
                    "(\n" +
                    "    Athlete_ID integer,\n" +
                    "    Sport_ID integer,\n" +
                    "    Payment integer default(0),\n"+
                    " FOREIGN KEY (Athlete_ID) references Athlete ON DELETE CASCADE,\n"+
                    " FOREIGN KEY (Sport_ID) references Sport ON DELETE CASCADE,\n"+
                    " PRIMARY KEY (Athlete_ID, Sport_ID)\n" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace();
        }

        try {
            pstmt = connection.prepareStatement("CREATE TABLE Winning\n" +
                    "(\n" +
                    "    Athlete_ID integer,\n" +
                    "    Sport_ID integer,\n" +
                    "    Place integer ,\n"+
                    " FOREIGN KEY (Athlete_ID) references Athlete ON DELETE CASCADE,\n"+
                    " FOREIGN KEY (Sport_ID) references Sport ON DELETE CASCADE,\n"+
                    " PRIMARY KEY (Athlete_ID, Sport_ID),\n" +
                    "CHECK (Place BETWEEN 1 AND 3)"+

                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }

        try {

            pstmt = connection.prepareStatement("CREATE TABLE Friendship\n" +
                    "(\n" +
                    "    Athlete_ID1 integer,\n" +
                    "    Athlete_ID2 integer,\n" +
                    " FOREIGN KEY (Athlete_ID1) references Athlete(Athlete_ID) ON DELETE CASCADE,\n"+
                    " FOREIGN KEY (Athlete_ID2) references Athlete(Athlete_ID) ON DELETE CASCADE,\n"+
                    " PRIMARY KEY (Athlete_ID1, Athlete_ID2)\n" +
                    ")");
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

    }

    public static void clearTables() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE  FROM  Friendship");
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE  FROM Winning");
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE  FROM AthleteStatus");
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE  FROM Sport");
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE  FROM Athlete");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
    }

    public static void dropTables() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Friendship");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Winning");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS AthleteStatus");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Sport");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS Athlete");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
    }

    public static ReturnValue addAthlete(Athlete athlete) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue r=null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Athlete" +
                    " VALUES (?, ?, ?, ?)");
            pstmt.setInt(1,athlete.getId());
            pstmt.setString(2, athlete.getName());
            pstmt.setString(3, athlete.getCountry());
            pstmt.setBoolean(4, athlete.getIsActive());
            pstmt.execute();
        } catch (SQLException e) {
            r= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
        if (r!=null)
            return r;
        return OK;
    }

    public static Athlete getAthleteProfile(Integer athleteId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        Athlete a= null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM Athlete WHERE Athlete_ID = (?)");
            pstmt.setInt(1,athleteId);
            ResultSet results = pstmt.executeQuery();
            a= new Athlete();
            results.next();
            a.setId(results.getInt(1));
            a.setCountry(results.getString(2));
            a.setName(results.getString(3));
            a.setIsActive(results.getBoolean(4));
            results.close();

        } catch (SQLException e) {
            return Athlete.badAthlete();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return a;
    }

    public static ReturnValue deleteAthlete(Athlete athlete) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue=null;
//        boolean active= athlete.getIsActive();
//        if (active) {
//            try {
//                pstmt = connection.prepareStatement(
//                                "UPDATE Sport\n" +
//                                "SET Athletes_Counter = Athletes_Counter-1 \n" +
//                                "WHERE Sport_ID = (SELECT Sport_ID FROM  AthleteStatus\n" +
//                                        "WHERE id = ?)");
//            }catch (SQLException e){
//                returnValue= translateException(e);
//            }finally {
//                if (returnValue!=null){try {
//                    pstmt.close();
//                } catch (SQLException e) {
//                    //e.printStackTrace()();
//                }
//                    try {
//                        connection.close();
//                    } catch (SQLException e) {
//                        //e.printStackTrace()();
//                    }
//                    return returnValue;
//                }
//            }
//
//        }

        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Athlete " +
                            "where Athlete_ID = ?");
            pstmt.setInt(1,athlete.getId());

            int affectedRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        if (returnValue!=null)
            return returnValue;
        return OK;
    }

    public static ReturnValue addSport(Sport sport) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue r=null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Athlete" +
                    " VALUES (?, ?, ?, ?)");
            pstmt.setInt(1,sport.getId());
            pstmt.setString(2, sport.getName());
            pstmt.setString(3, sport.getCity());
            pstmt.setInt(4, sport.getAthletesCount());
            pstmt.execute();
        } catch (SQLException e) {
            r= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
        if (r!=null)
            return r;
        return OK;
    }

    public static Sport getSport(Integer sportId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        Sport sport= null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM Sport WHERE Sport_ID = (?)");
            pstmt.setInt(1,sportId);
            ResultSet results = pstmt.executeQuery();
            sport= new Sport();
            results.next();
            sport.setId(results.getInt(1));
            sport.setName(results.getString(2));
            sport.setCity(results.getString(3));
            sport.setAthletesCount(results.getInt(4));
            results.close();

        } catch (SQLException e) {
            return Sport.badSport();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return sport;
    }

    public static ReturnValue deleteSport(Sport sport) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue=null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Sport " +
                            "where Sport_ID = ?");
            pstmt.setInt(1,sport.getId());
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        if (returnValue!=null)
            return returnValue;
        return OK;
    }

    public static ReturnValue athleteJoinSport(Integer sportId, Integer athleteId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue=null;
        try {
            //Insert to AthleteStatus
            pstmt = connection.prepareStatement("INSERT INTO AthleteStatus" +
                    " VALUES (?, ?, ?)");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2, athleteId);
            pstmt.setInt(3, 0);
            pstmt.execute();

            //Update the paycheck
            pstmt = connection.prepareStatement("UPDATE AthleteStatus" +
                    "SET payment=100" +
                    "WHERE Athlete_ID=" +
                    "(SELECT Athlete_ID " +
                    "FROM Athlete " +
                    "WHRER Athlete_id=? and isactive=true)");
            pstmt.setInt(1,athleteId);
            pstmt.execute();

            //Upadate the athlete counter if necessary
            pstmt = connection.prepareStatement("UPDATE Sport" +
                    "SET AthletesCounter=AthletesCounter+1" +
                    "WHERE Sport_ID=?" +
                    "AND WHERE Athlete_ID=" +
                    "(SELECT Athlete_ID " +
                    "FROM Athlete " +
                    "WHRER Athlete_id=? and isactive=true)");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2,athleteId);
            pstmt.execute();

        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        if (returnValue!=null)
            return returnValue;
        return OK;

    }

    public static ReturnValue athleteLeftSport(Integer sportId, Integer athleteId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue=null;
        try {
            pstmt = connection.prepareStatement("UPDATE Sport" +
                    "SET AthletesCounter=AthletesCounter-1" +
                    "WHERE Sport_ID=?" +
                    "AND WHERE Athlete_ID=" +
                    "(SELECT Athlete_ID " +
                    "FROM Athlete " +
                    "WHRER Athlete_id=? and isactive=true)");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2,athleteId);
            pstmt.execute();

            pstmt = connection.prepareStatement("DELETE FROM AthleteStatus" +
                    "WHERE Sport_ID= ?, Athlete_ID= ?");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2,athleteId);
            pstmt.execute();

        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        if (returnValue!=null)
            return returnValue;
        return OK;
    }

    public static ReturnValue confirmStanding(Integer sportId, Integer athleteId, Integer place) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue= null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM Winning " +
                    "WHERE Sport_ID = (?) AND Athlete_ID=? AND Place=?");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2,athleteId);
            pstmt.setInt(3,place);
            ResultSet results = pstmt.executeQuery();
            if (!results.next())
                returnValue= NOT_EXISTS;

            results.close();


        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        if (returnValue!=null)
            return returnValue;
        return OK;
    }

    public static ReturnValue athleteDisqualified(Integer sportId, Integer athleteId) {

        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue= OK;
        try {
            pstmt = connection.prepareStatement("DELETE FROM Winning" +
                    "WHERE Sport_ID= ?, Athlete_ID= ?");
            pstmt.setInt(1,sportId);
            pstmt.setInt(2,athleteId);
            pstmt.execute();
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static ReturnValue makeFriends(Integer athleteId1, Integer athleteId2) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue= OK;
        try {
            pstmt = connection.prepareStatement("IF NOT EXISTS" +
                    "(SELECT Athlete_ID1 , Athlete_ID1 " +
                    "FROM Friendship WHERE Athlete_ID1 = ?, Athlete_ID2 = ?\n" +
                    "    INSERT INTO Friendship" +
                    "    VALUES(?,?)");
            pstmt.setInt(1,athleteId2);
            pstmt.setInt(2,athleteId1);
            pstmt.setInt(3,athleteId1);
            pstmt.setInt(4,athleteId2);
            pstmt.execute();
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static ReturnValue removeFriendship(Integer athleteId1, Integer athleteId2) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue= OK;
        try {
            pstmt = connection.prepareStatement("DELETE FROM Friendship" +
                    "WHERE Athlete_ID1= ?, Athlete_ID2= ? OR Athlete_ID1= ?, Athlete_ID2= ?");
            pstmt.setInt(1,athleteId2);
            pstmt.setInt(2,athleteId1);
            pstmt.setInt(3,athleteId1);
            pstmt.setInt(4,athleteId2);
            pstmt.execute();
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static ReturnValue changePayment(Integer athleteId, Integer sportId, Integer payment) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ReturnValue returnValue= OK;
        try {
            pstmt = connection.prepareStatement("UPDATE AthleteStatus" +
                    "SET payment= ?" +
                    "WHERE Athlete_ID1= ? Sport_ID=?");
            pstmt.setInt(1,payment);
            pstmt.setInt(2,athleteId);
            pstmt.setInt(3,sportId);
            pstmt.execute();
        } catch (SQLException e) {
            returnValue= translateException(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static Boolean isAthletePopular(Integer athleteId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        boolean returnValue= false;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW FriendSport" +
                    "AS SELECT Sport_ID FROM AthleteStatus" +
                    "WHERE Athelete_ID IN" +
                    "(SELECT Athelete_ID1 FROM Friendship WHERE Athelete_ID2=?)" +
                    "OR (SELECT Athelete_ID2 FROM Friendship WHERE Athelete_ID1=?)");
            pstmt.setInt(1, athleteId);
            pstmt.setInt(2, athleteId);
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT Sport_ID" +
                    "FROM FriendSport" +
                    "WHERE Sport_ID NOT IN (SELECT Sport_ID FROM AthleteStatus" +
                    "WHERE Athlete_ID=?)");
            pstmt.setInt(1,athleteId);
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            returnValue=!results.next();
            results.close();

            pstmt = connection.prepareStatement("DROP VIEW FriendSport");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static Integer getTotalNumberOfMedalsFromCountry(String country) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        int returnValue= 0;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW AthleteFrom" +
                    "AS SELECT Athlete_ID FROM Athlete" +
                    "WHERE Country=?");
            pstmt.setString(1, country);
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT COUNT (Athlete_ID) FROM Winning" +
                    " WHERE Athlete_ID IN AthleteFrom ");
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            returnValue= results.getInt(1);

            pstmt = connection.prepareStatement("DROP VIEW AthleteFrom");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;

    }

    public static Integer getIncomeFromSport(Integer sportId) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        int returnValue= 0;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW Observer" +
                    "AS SELECT Athlete_ID FROM Athlete" +
                    "WHERE Active=FALSE");
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT SUM (payment) FROM AthleteStatus" +
                    " WHERE Sport_ID= ? AND Athlete_ID IN Observer");
            pstmt.setInt(1, sportId);
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            returnValue= results.getInt(1);

            pstmt = connection.prepareStatement("DROP VIEW Observer");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static String getBestCountry() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        String returnValue= null;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW AthleteAndCountries" +
                    "AS SELECT Winning.Athlete_ID, Country " +
                    "FROM Athlete, Winning" +
                    "WHERE Winning.Athlete_ID=Athlete.Athlete_ID");
            pstmt.execute();

            pstmt = connection.prepareStatement("CREATE VIEW WinningFromCountry" +
                    "SELECT country COUNT (Athlete_ID) AS WinnersFrom" +
                    "FROM AthleteAndCountries" +
                    "GROUP BY Contry");
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT Country, MAX(WinnersFrom)" +
                    "ORDER BY Country");
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            if (results.next())
                returnValue= results.getString(1);
            else
                returnValue="";

            results.close();

            pstmt = connection.prepareStatement("DROP VIEW AthleteAndCountries");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP VIEW WinningFromCountry");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static String getMostPopularCity() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        String returnValue= null;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW SportsByCity" +
                    "AS SELECT city SUM(AthletesCounter) As Athletes COUNT (Sport_ID) As Sports" +
                    "FROM Sport" +
                    "GROUP BY City");
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT City, MAX(Athletes/Sports)" +
                    "ORDER BY City");
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            if (results.next())
                returnValue= results.getString(1);
            else
                returnValue="";

            results.close();

            pstmt = connection.prepareStatement("DROP VIEW SportsByCity");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return returnValue;
    }

    public static ArrayList<Integer> getAthleteMedals(Integer athleteId) {
        ArrayList array= new ArrayList<>();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("CREATE VIEW AthleteMedal" +
                    "AS SELECT place" +
                    "FROM Winning" +
                    "WHERE Athlete_ID=?");
            pstmt.setInt(1, athleteId);
            pstmt.execute();

            pstmt = connection.prepareStatement("SELECT place COUNT (place)" +
                    "WHERE place=1");
            pstmt.execute();

            ResultSet results = pstmt.executeQuery();
            if (results.next())
                array.add(results.getString(1));
            else
                array.add(results.getString(1));
            results.close();

            pstmt = connection.prepareStatement("SELECT place COUNT (place)" +
                    "WHERE place=1");
            pstmt.execute();

            results = pstmt.executeQuery();
            if (results.next())
                array.add(results.getString(1));
            else
                array.add(0);
            results.close();

            pstmt = connection.prepareStatement("SELECT place COUNT (place)" +
                    "WHERE place=2");
            pstmt.execute();

            results = pstmt.executeQuery();
            if (results.next())
                array.add(results.getString(1));
            else
                array.add(0);
            results.close();

            pstmt = connection.prepareStatement("SELECT place COUNT (place)" +
                    "WHERE place=3");
            pstmt.execute();

            results = pstmt.executeQuery();
            if (results.next())
                array.add(results.getString(1));
            else
                array.add(0);
            results.close();


            pstmt = connection.prepareStatement("DROP VIEW AthleteMedal");
            pstmt.execute();

        } catch (SQLException e) {

        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }

        return array;
    }

    public static ArrayList<Integer> getMostRatedAthletes() {
        return new ArrayList<>();
    }

    public static ArrayList<Integer> getCloseAthletes(Integer athleteId) {
        return new ArrayList<>();
    }

    public static ArrayList<Integer> getSportsRecommendation(Integer athleteId) {
        return new ArrayList<>();
    }

    private static ReturnValue translateException(SQLException e){
        if(Integer.valueOf(e.getSQLState()) ==
                PostgreSQLErrorCodes.CHECK_VIOLIATION.getValue())
            return BAD_PARAMS;
        if (Integer.valueOf(e.getSQLState()) ==
                PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue())
            return BAD_PARAMS;
        if (Integer.valueOf(e.getSQLState()) ==
                PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
            return NOT_EXISTS;
        if (Integer.valueOf(e.getSQLState()) ==
                PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
            return ALREADY_EXISTS;

        return ERROR;

    }
}

