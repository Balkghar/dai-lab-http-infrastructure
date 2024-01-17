function createCommentElement(comment) {
    var li = document.createElement("li");
    var span = document.createElement("span");
    var newContentLi = document.createTextNode(comment.author + ": ");
    var newContentSpan = document.createTextNode(comment.content);
    li.appendChild(newContentLi);
    span.appendChild(newContentSpan);
    li.appendChild(span);
    return li;
}

function fetchComments(blogId, ul) {
    fetch(`${window._env_.URL}/api/blogs/${blogId}/comments`)
        .then((response) => {
            if (!response.ok) {
                // Check if the response was not ok
                if (response.status === 404) {
                    // Check if the status was 404
                    // Handle 404 error
                    const noCommentsMessage = document.createElement("li");
                    noCommentsMessage.textContent = "No comments yet.";
                    ul.appendChild(noCommentsMessage);
                } else {
                    // Handle other errors
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } else {
                return response.json();
            }
        })
        .then((comments) => {
            if (comments) {
                comments.forEach((comment) => {
                    const commentElement = createCommentElement(comment);
                    ul.appendChild(commentElement);
                });
            }
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}

function createDivElement(blog, includeLink = true) {
    var h2 = document.createElement("h2");
    var p = document.createElement("p");
    var newContenth2 = document.createTextNode(blog.title);
    var newContentp = document.createTextNode(blog.content);
    p.appendChild(newContentp);
    h2.appendChild(newContenth2);
    var article = document.createElement("article");
    article.dataset.id = blog._id; // Add the blog post's id as a data attribute
    article.appendChild(h2);
    article.appendChild(p);

    if (includeLink) {
        var a = document.createElement("a");
        a.textContent = "Read more";
        a.href = `/?id=${blog._id}`; // Link to the blog post
        article.appendChild(a);
    }

    var h3 = document.createElement("h3");
    h3.textContent = "Comments";
    article.appendChild(h3);

    var ul = document.createElement("ul");
    article.appendChild(ul);

    fetchComments(blog._id, ul);

    article.appendChild(createCommentForm(blog._id));

    // Create the Delete button
    var deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.addEventListener("click", function () {
        deleteBlog(blog._id);
    });
    article.appendChild(deleteButton);

    // Create the Edit button
    var editButton = document.createElement("button");
    editButton.textContent = "Edit";
    article.appendChild(editButton);

    // Create the Edit form
    var editForm = document.createElement("form");
    var titleInput = document.createElement("input");
    var contentInput = document.createElement("textarea");
    var submitButton = document.createElement("button");

    titleInput.placeholder = "New Blog Title";
    contentInput.placeholder = "New Blog Content";
    submitButton.textContent = "Update Blog";

    editForm.appendChild(titleInput);
    editForm.appendChild(contentInput);
    editForm.appendChild(submitButton);
    editForm.style.display = "none"; // Hide the form by default

    article.appendChild(editForm);

    // Add an event listener to the Edit button
    editButton.addEventListener("click", function () {
        fetchBlogData(blog._id).then((updatedBlog) => {
            // Populate the form with the updated blog details
            titleInput.value = updatedBlog.title;
            contentInput.value = updatedBlog.content;

            editForm.style.display = "block"; // Show the form when the Edit button is clicked
        });
    });

    // Add an event listener to the Edit form
    editForm.addEventListener("submit", function (event) {
        event.preventDefault();
        updateBlog(blog._id, titleInput.value, contentInput.value);
        titleInput.value = "";
        contentInput.value = "";
        editForm.style.display = "none"; // Hide the form after submission
    });

    return article;
}

document.addEventListener("DOMContentLoaded", () => {
    // Get the query parameters from the URL
    const urlParams = new URLSearchParams(window.location.search);

    // Get the id parameter
    const id = urlParams.get("id");

    // Function to fetch and display blog posts
    const fetchAndDisplayPosts = () => {
        if (id) {
            // If an id parameter is present, fetch the corresponding blog post and its comments
            fetchSinglePost(id);
        } else {
            // If no id parameter is present, fetch all blog posts
            fetch(`${window._env_.URL}/api/blogs`)
                .then((response) => response.json())
                .then((blogs) => {
                    // Clear the main element
                    document.querySelector("main").innerHTML = '';
                    blogs.forEach((blog) => {
                        const blogElement = createDivElement(blog, true);
                        document.querySelector("main").appendChild(blogElement);
                    });
                })
                .catch((error) => {
                    console.error("Error:", error);
                })
                .finally(() => {
                    // Create the blog form after all blogs have been fetched and displayed
                    createBlogForm();
                });
        }
    };

    // Fetch and display posts initially
    fetchAndDisplayPosts();

    // Update the website every 10 seconds
    setInterval(fetchAndDisplayPosts, 10000);
});

function fetchSinglePost(blogId) {
    fetch(`${window._env_.URL}/api/blogs/${blogId}`)
        .then((response) => response.json())
        .then((blog) => {
            const blogElement = createDivElement(blog, false);
            document.querySelector("main").appendChild(blogElement);
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}

function createBlogForm() {
    var form = document.createElement("form");
    var titleInput = document.createElement("input");
    var contentInput = document.createElement("textarea");
    var submitButton = document.createElement("button");

    titleInput.placeholder = "Blog Title";
    titleInput.required = true;
    contentInput.placeholder = "Blog Content";
    contentInput.required = true;
    submitButton.textContent = "Create Blog";

    form.appendChild(titleInput);
    form.appendChild(contentInput);
    form.appendChild(submitButton);

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        createNewBlog(titleInput.value, contentInput.value);
        titleInput.value = "";
        contentInput.value = "";
    });

    document.querySelector("main").appendChild(form);
}

function createNewBlog(title, content) {
    const blogData = {
        title: title,
        content: content,
    };

    fetch(`${window._env_.URL}/api/blogs`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(blogData),
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Success:", data);
            const newBlogElement = createDivElement(data, true);
            const form = document.querySelector("form");
            // Insert the new blog before the form
            form.parentNode.insertBefore(newBlogElement, form);
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}
function updateBlog(blogId, newTitle, newContent) {
    fetch(`${window._env_.URL}/api/blogs/${blogId}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            title: newTitle,
            content: newContent,
        }),
    })
        .then((response) => response.json())
        .then((updatedBlog) => {
            // Update the blog post in the DOM with the updated blog details
            const blogElement = document.querySelector(
                `article[data-id="${blogId}"]`
            );
            blogElement.querySelector("h2").textContent = updatedBlog.title;
            blogElement.querySelector("p").textContent = updatedBlog.content;
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}
function fetchBlogData(blogId) {
    return fetch(`${window._env_.URL}/api/blogs/${blogId}`)
        .then((response) => response.json())
        .catch((error) => {
            console.error("Error:", error);
        });
}
function deleteBlog(blogId) {
    fetch(`${window._env_.URL}/api/blogs/${blogId}`, {
        method: "DELETE",
    })
        .then((response) => {
            if (response.ok) {
                // If the response was OK, remove the blog post from the DOM
                const blogElement = document.querySelector(
                    `article[data-id="${blogId}"]`
                );
                blogElement.remove();
            } else {
                console.error("Error:", response.statusText);
            }
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}
function addComment(blogId, author, content) {
    fetch(`${window._env_.URL}/api/blogs/${blogId}/comments`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            _blogId: blogId,
            author: author,
            content: content,
        }),
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Success:", data);
            const commentElement = createCommentElement(data);
            const ul = document.querySelector(`article[data-id="${blogId}"] ul`);
            ul.appendChild(commentElement);
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}
function createCommentForm(blogId) {
    var form = document.createElement("form");
    var authorInput = document.createElement("input");
    var contentInput = document.createElement("textarea");
    var submitButton = document.createElement("button");

    authorInput.placeholder = "Your Name";
    contentInput.placeholder = "Your Comment";
    submitButton.textContent = "Add Comment";

    form.appendChild(authorInput);
    form.appendChild(contentInput);
    form.appendChild(submitButton);

    form.onsubmit = function (event) {
        event.preventDefault();
        addComment(blogId, authorInput.value, contentInput.value);
        authorInput.value = "";
        contentInput.value = "";
    };

    return form;
}
