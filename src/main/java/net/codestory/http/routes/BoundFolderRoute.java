/**
 * Copyright (C) 2013-2014 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http.routes;

import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.io.Strings.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.payload.*;
import net.codestory.http.types.*;

class BoundFolderRoute implements Route {
	private final String uriRoot;
	private final File root;

	public BoundFolderRoute(String uriRoot, File root) {
		this.uriRoot = addLeadingSlash(uriRoot);
		this.root = root;
	}

	@Override
	public boolean matchUri(String uri) {
		return uri.startsWith(uriRoot);
	}

	@Override
	public boolean matchMethod(String method) {
		return GET.equalsIgnoreCase(method) || HEAD.equalsIgnoreCase(method);
	}

	@Override
	public Payload body(Context context) throws IOException {
		File file = new File(root, substringAfter(context.uri(), uriRoot));
		if (!isPublic(file)) {
			return Payload.notFound();
		}

		try (InputStream from = new FileInputStream(file)) {
			return new Payload(ContentTypes.get(file.getName()), InputStreams.readBytes(from));
		}
	}

	public boolean isPublic(File file) {
		for (Path part : file.toPath()) {
			if ("..".equals(part.toString()) || part.toString().startsWith("_")) {
				return false;
			}
		}
		return file.exists();
	}

	private static String addLeadingSlash(String uriRoot) {
		return uriRoot.endsWith("/") ? uriRoot : uriRoot + "/";
	}
}
