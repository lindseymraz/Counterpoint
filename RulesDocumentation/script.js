//initialize dropdowns to all start closed
document.querySelectorAll(".dropdown").forEach(dropdown => {
    dropdown.addEventListener("click", dropdownFn);
    dropdown.setAttribute("class", "dropdown closed");
    for(let i = 0; i < dropdown.children.length; i++) {
        dropdown.children.item(i).style.display = "none";
    }
});

function toggleDropdown(dropdown) {
    for(let i = 0; i < dropdown.children.length; i++) {
        dropdown.children.item(i).style.display = (dropdown.className.includes("closed")?"block":"none");
    }
    dropdown.className = dropdown.className.includes("closed")?"dropdown open":"dropdown closed";
}

function dropdownFn(e) {
    e.stopPropagation();
    if(e.target.classList.contains("dropdown")) {
        toggleDropdown(e.target);
    }
}

//TODO: Refactor. Is selectByClass better?
//TODO: Nav links if this gets bigger/longer
//TODO: Text spacing
//TODO: Make dropdowns prettier, perhaps with Notion or Obsidian-like arrows. Also, colors when mousing over need to be nicer. Probably copy Wikipedia.
//TODO: Make highlighting not span the whole page, or at least how Wikipedia formats their references. Probably needs reading the way CSS works.
//TODO: Better fonts, this looks so boring and also intimidating.
//TODO: Make accessible, because "none" display doesn't read out to screen readers!