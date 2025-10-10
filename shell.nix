{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShellNoCC {
  LD_LIBRARY_PATH=pkgs.lib.strings.makeLibraryPath [pkgs.libglvnd pkgs.zlib];
  shellHook = ''
    apply() {
      echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH" > .env
    }
  '';
  packages = with pkgs; [(python3.withPackages (pp: [pp.pip]))];
}