/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.hubsuite.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.common.base.Charsets;

public class Pinger {

	public static boolean isOnline(InetSocketAddress serverAddress, int timeout) {
		Socket socket = null;
		DataOutputStream dataOut = null;
		DataInputStream dataIn = null;

		try {
			socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
			socket.setSoTimeout(timeout);
			dataOut = new DataOutputStream(socket.getOutputStream());
			dataIn = new DataInputStream(socket.getInputStream());
			final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			final DataOutputStream handshake = new DataOutputStream(byteOut);
			handshake.write(0);
			writeVarInt(handshake, 4);
			writeString(handshake, serverAddress.getAddress().getHostAddress());
			handshake.writeShort(serverAddress.getPort());
			writeVarInt(handshake, 1);
			byte[] bytes = byteOut.toByteArray();
			writeVarInt(dataOut, bytes.length);
			dataOut.write(bytes);
			bytes = new byte[] { 0 };
			writeVarInt(dataOut, bytes.length);
			dataOut.write(bytes);
			readVarInt(dataIn);
			readVarInt(dataIn);
			final byte[] responseData = new byte[readVarInt(dataIn)];
			dataIn.readFully(responseData);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			closeQuietly(dataOut);
			closeQuietly(dataIn);
			closeQuietly(socket);
		}
	}

	public static void writeString(final DataOutputStream out, final String s) throws IOException {
		writeVarInt(out, s.length());
		out.write(s.getBytes(Charsets.UTF_8));
	}

	public static int readVarInt(final DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			final int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((k & 0x80) != 0x80) {
				return i;
			}
		}
	}

	public static void writeVarInt(final DataOutputStream out, int paramInt) throws IOException {
		while ((paramInt & 0xFFFFFF80) != 0x0) {
			out.write((paramInt & 0x7F) | 0x80);
			paramInt >>>= 7;
		}
		out.write(paramInt);
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {}
	}

}
