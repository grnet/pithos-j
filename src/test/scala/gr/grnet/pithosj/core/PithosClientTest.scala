/*
 * Copyright (C) 2010-2014 GRNET S.A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.grnet.pithosj.core

import java.io.{File, FileOutputStream}
import java.net.URL

import com.twitter.io._
import com.twitter.util._
import gr.grnet.pithosj.TestBase
import gr.grnet.pithosj.impl.finagle.PithosClientFactory
import org.junit.Test

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
class PithosClientTest extends TestBase {
  lazy val SERVER_URL = System.getProperty("SERVER_URL") // https://pithos.okeanos.grnet.gr
  lazy val ROOT_PATH  = System.getProperty("ROOT_PATH")  // /object-store/v1
  lazy val UUID = System.getProperty("UUID")
  lazy val TOKEN = System.getProperty("TOKEN")
  lazy val info = new ServiceInfo(new URL(SERVER_URL), ROOT_PATH, UUID, TOKEN)
  lazy val pithos = PithosClientFactory.newClient(info)

//  @Test def dummy(): Unit = {
//    // This is to avoid any exceptions like:
//    // - java.lang.Exception: No runnable methods
//  }

//  @Test
//  def getAccountInfo(): Unit = {
//    val future = pithos.getAccountInfo(info)
//    assertFuture(future)
//    Await.result(future)
//  }

//  @Test
//  def listContainers(): Unit = {
//    val f = pithos.listContainers(info)
//    assertFuture(f)
//  }

//  @Test
//  def getObjectInfo(): Unit = {
//    val future = pithos.getObjectInfo(info, "pithos", "Papers/10.1.1.115.1568.pdf")
//    assertFuture(future)
//  }

//  @Test
//  def getObject(): Unit = {
//    val out = new FileOutputStream("/tmp/a.pdf")
//    val future = pithos.getObject(info, "pithos", "Papers/10.1.1.115.1568.pdf", "332033", out)
//    assertFuture(future)
//    out.close()
//  }

//  @Test
//  def getObject2(): Unit = {
//    val future = pithos.getObject2(info, "pithos/wadler87.pdf", "332033")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//      } {
//        val buf = data.objBuf
//        val reader = BufReader(buf)
//        val out = new FileOutputStream("/tmp/a.pdf")
//        val writer = Writer.fromOutputStream(out)
//        val copyF = Reader.copy(reader, writer)
//        val copyF2 = copyF.ensure {
//          writer.close()
//          out.close()
//        }
//
//        Await.ready(copyF2)
//      }
//    }
//  }

//  @Test
//  def putObject(): Unit = {
//    val in = new File("./wadler87.pdf")
//    val size = in.length()
//    println("size = " + size)
//    val future = pithos.putObject(info, "pithos", "wadler107.pdf", in, "application/pdf")
//    assertFuture(future)
//  }

//  @Test
//  def putObject2(): Unit = {
//    val in = new File("/Users/loverdos/Downloads/wadler87.pdf")
//    val size = in.length()
//    println("size = " + size)
//    val future = pithos.putObject(info, "/pithos/wadler107.pdf", in, "application/pdf")
//    assertFuture(future)
//  }

//  @Test
//  def checkExistsFile(): Unit = {
//    val future = pithos.checkExistsObject(info, "pithos", "/wadler87.pdf")
//    assertFuture(future)
//  }

//  @Test
//  def checkExistsFile2(): Unit = {
//    val future = pithos.checkExistsObject(info, "pithos/wadler87.pdf")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//      } {
//        println(data.isContainer)
//        println(data.isDirectory)
//      }
//    }
//  }

//  @Test
//  def checkExistsFile3(): Unit = {
//    val future = pithos.checkExistsObject(info, "pithos/")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//      } {
//        println(data.contentType)
//        println(data.isContainer)
//        println(data.isDirectory)
//      }
//    }
//  }

//  @Test
//  def checkExistsContainer(): Unit = {
//    val future = pithos.checkExistsContainer(info, "pithos")
//    assertFuture(future)
//  }

//  @Test
//  def deleteFile(): Unit = {
//    val future = pithos.deleteFile(info, "pithos", "/wadler87.pdf")
//    assertFuture(future)
//  }

//  @Test
//  def deleteFile2(): Unit = {
//    val future = pithos.deleteFile(info, "pithos/wadler87.pdf")
//    assertFuture(future)
//  }

//  @Test
//  def listObjectsInPath(): Unit = {
//    val future = pithos.listObjectsInPath(info, "pithos", "Papers")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//        obj ← data.objects
//      } {
//        println(obj)
//      }
//    }
//  }

//  @Test
//  def listObjectsInPath2(): Unit = {
//    val future = pithos.listObjectsInPath(info, "pithos/Papers")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//        obj ← data.objects
//      } {
//        println(obj)
//      }
//    }
//  }

//  @Test
//  def listObjectsInPath3(): Unit = {
//    val future = pithos.listObjectsInPath(info, "pithos")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//        obj ← data.objects
//      } {
//        println(obj)
//      }
//    }
//  }

//  @Test
//  def listObjectsInContainer(): Unit = {
//    val future = pithos.listObjectsInContainer(info, "pithos")
//    assertResultX(future) { resultData ⇒
//      for {
//        data ← resultData.successData
//        obj ← data.objects
//      } {
//        println(obj)
//      }
//    }
//  }

//  @Test
//  def createDirectory(): Unit = {
//    val future = pithos.createDirectory(info, "pithos", "foobar_2")
//    assertFuture(future)
//  }

//  @Test
//  def createDirectory2(): Unit = {
//    val future = pithos.createDirectory(info, "pithos/foobar_3")
//    assertFuture(future)
//  }

//  @Test
//  def deleteDirectory(): Unit = {
//    val future = pithos.deleteDirectory(info, "pithos", "foobar_2")
//    assertFuture(future)
//  }

//  @Test
//  def deleteDirectory2(): Unit = {
//    val future = pithos.deleteDirectory(info, "pithos/foobar_2")
//    assertFuture(future)
//  }

//  @Test
//  def copyObject(): Unit = {
//    val fromContainer = "pithos"
//    val toContainer = fromContainer
//    val fromPath = "wadler89.pdf"
//    val toPath = fromPath + ".backup"
//    val future = pithos.copyObject(info, fromContainer, fromPath, toContainer, toPath)
//    assertFuture(future)
//  }

//  @Test
//  def copyObject2(): Unit = {
//    val future = pithos.copyObject(info, "pithos/wadler87.pdf", "pithos/wadler888.pdf")
//    assertFuture(future)
//  }

//  @Test
//  def combinedCallWithFor(): Unit = {
//    val f =
//      for {
//        a ← pithos.checkExistsObject(info, "pithos", "/wadler87.pdf")
//        b ← pithos.listObjectsInPath(info, "pithos")
//      } yield (a, b)
//
//    assertResult(f)
//  }

//  @Test
//  def combinedCallWithTransform(): Unit = {
//    val f1 = pithos.checkExistsObject(info, "pithos", "/wadler87.pdf")
//    val f3 = f1.transform {
//      case Return(r1) ⇒
//        val f2 = pithos.listObjectsInPath(info, "pithos")
//        f2
//
//      case Throw(t1) ⇒
//        Future.rawException(t1)
//    }
//
//    Await.result(f3).successData
//  }
}
